package com.simonbaars.codearena.challenge;

import com.simonbaars.codearena.monster.SmellMobFactory;
import com.simonbaars.codearena.problem.CodeProblem;
import com.simonbaars.codearena.problem.DemoProblems;
import com.simonbaars.codearena.problem.ProblemTips;
import com.simonbaars.codearena.problem.ProblemType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

/**
 * Session stand-in for legacy {@code CodeArena}: schematic arena + watchtowers,
 * demo metric problems as typed smell mobs, package-filter diamonds, sidebar score.
 */
public final class ArenaSession {
	private static final String OBJECTIVE_ID = "codearena_score";
	private static final String SHOW_ALL = "Show All Packages";

	private final ServerLevel level;
	private final BlockPos center;
	private final UUID owner;
	private final List<UUID> spawnedMobs = new ArrayList<>();
	private final Map<UUID, CodeProblem> mobProblems = new HashMap<>();
	private final List<CodeProblem> problems;
	private final EnumMap<ProblemType, Integer> resolved = new EnumMap<>(ProblemType.class);
	private final ArenaBuilder.BuildResult buildResult;
	private final boolean previousMobGriefing;
	private Objective scoreObjective;
	private ScoreAccess totalScore;
	private int scorePoints;
	private boolean active = true;
	private String currentFilter = SHOW_ALL;

	private ArenaSession(ServerLevel level, BlockPos center, UUID owner,
			List<CodeProblem> problems, ArenaBuilder.BuildResult buildResult, boolean previousMobGriefing) {
		this.level = level;
		this.center = center;
		this.owner = owner;
		this.problems = List.copyOf(problems);
		this.buildResult = buildResult;
		this.previousMobGriefing = previousMobGriefing;
		for (ProblemType type : ProblemType.values()) {
			resolved.put(type, 0);
		}
	}

	public static ArenaSession spawn(ServerLevel level, ServerPlayer player) {
		BlockPos center = player.blockPosition();
		boolean prevGriefing = level.getGameRules().get(GameRules.MOB_GRIEFING);
		level.getGameRules().set(GameRules.MOB_GRIEFING, false, level.getServer());

		ArenaBuilder.BuildResult built = ArenaBuilder.build(level, center);
		player.teleportTo(center.getX() + 0.5, center.getY() + 3.0, center.getZ() + 0.5);
		player.getInventory().add(new ItemStack(Items.DIAMOND_SWORD));

		List<CodeProblem> problems = DemoProblems.sampleWave();
		ArenaSession session = new ArenaSession(level, center, player.getUUID(), problems, built, prevGriefing);
		session.givePackageFilterDiamonds(player);
		session.setupScoreboard();
		session.spawnProblems();

		String source = String.join("+", built.placedStructures());
		player.sendSystemMessage(Component.literal(
				"CodeArena ready (" + source + ", " + built.totalBlocks() + " blocks). Defeat "
						+ problems.size() + " metric smells (" + ProblemType.values().length
						+ " types). Hold a named diamond to filter by package. "
						+ "Demo problems only — CloneRefactor jar still required for real AST. "
						+ "/codearena problems | /codearena place <structure>"));
		return session;
	}

	private void givePackageFilterDiamonds(ServerPlayer player) {
		ItemStack showAll = new ItemStack(Items.DIAMOND);
		showAll.set(DataComponents.CUSTOM_NAME, Component.literal(SHOW_ALL));
		player.getInventory().add(showAll);

		Set<String> packages = new HashSet<>();
		for (CodeProblem problem : problems) {
			packages.add(problem.getPackageName());
		}
		for (String pkg : packages) {
			ItemStack diamond = new ItemStack(Items.DIAMOND);
			diamond.set(DataComponents.CUSTOM_NAME, Component.literal(pkg));
			player.getInventory().add(diamond);
		}
	}

	private void setupScoreboard() {
		Scoreboard board = level.getScoreboard();
		Objective existing = board.getObjective(OBJECTIVE_ID);
		if (existing != null) {
			board.removeObjective(existing);
		}
		scoreObjective = board.addObjective(
				OBJECTIVE_ID,
				ObjectiveCriteria.DUMMY,
				Component.literal("CodeArena"),
				ObjectiveCriteria.RenderType.INTEGER,
				false,
				null);
		board.setDisplayObjective(DisplaySlot.SIDEBAR, scoreObjective);
		totalScore = board.getOrCreatePlayerScore(ScoreHolder.forNameOnly("Score"), scoreObjective);
		totalScore.set(0);
		for (ProblemType type : ProblemType.values()) {
			board.getOrCreatePlayerScore(ScoreHolder.forNameOnly(type.scoreboardLabel()), scoreObjective).set(0);
		}
		board.getOrCreatePlayerScore(ScoreHolder.forNameOnly("Remaining"), scoreObjective).set(problems.size());
	}

	private void spawnProblems() {
		int count = problems.size();
		for (int i = 0; i < count; i++) {
			CodeProblem problem = problems.get(i);
			Mob mob = SmellMobFactory.create(level, problem);
			if (mob == null) {
				continue;
			}
			double angle = (Math.PI * 2 * i) / count;
			double radius = 7.0 + (i % 3);
			double ox = Math.cos(angle) * radius;
			double oz = Math.sin(angle) * (radius + 2.0);
			mob.setPos(center.getX() + 0.5 + ox, center.getY() + 2.0, center.getZ() + 0.5 + oz);
			level.addFreshEntity(mob);
			spawnedMobs.add(mob.getUUID());
			mobProblems.put(mob.getUUID(), problem);
		}
	}

	/** Legacy diamond package filter — hide smells outside the held package name. */
	public void tickFilter(ServerPlayer player) {
		if (!active || !player.getUUID().equals(owner)) {
			return;
		}
		ItemStack held = player.getMainHandItem();
		String filter = SHOW_ALL;
		if (held.is(Items.DIAMOND) && held.has(DataComponents.CUSTOM_NAME)) {
			filter = held.get(DataComponents.CUSTOM_NAME).getString();
		}
		if (filter.equals(currentFilter)) {
			return;
		}
		currentFilter = filter;
		boolean showAll = SHOW_ALL.equals(currentFilter);
		for (Map.Entry<UUID, CodeProblem> entry : mobProblems.entrySet()) {
			Entity e = level.getEntity(entry.getKey());
			if (e == null) {
				continue;
			}
			boolean visible = showAll || entry.getValue().getPackageName().equals(currentFilter);
			e.setInvisible(!visible);
		}
		player.sendSystemMessage(Component.literal("Package filter: " + currentFilter));
	}

	public void onEntityDeath(Entity entity) {
		if (!active) {
			return;
		}
		CodeProblem problem = mobProblems.remove(entity.getUUID());
		if (problem == null) {
			return;
		}
		spawnedMobs.remove(entity.getUUID());
		int points = Math.max(1, problem.getSeverity() / 2);
		scorePoints += points;
		resolved.merge(problem.getType(), 1, Integer::sum);

		if (totalScore != null) {
			totalScore.set(scorePoints);
		}
		Scoreboard board = level.getScoreboard();
		if (scoreObjective != null) {
			board.getOrCreatePlayerScore(ScoreHolder.forNameOnly(problem.getType().scoreboardLabel()), scoreObjective)
					.set(resolved.get(problem.getType()));
			board.getOrCreatePlayerScore(ScoreHolder.forNameOnly("Remaining"), scoreObjective)
					.set(mobProblems.size());
		}

		ServerPlayer ownerPlayer = level.getServer().getPlayerList().getPlayer(owner);
		if (ownerPlayer != null) {
			ownerPlayer.sendSystemMessage(Component.literal(
					"Resolved +" + points + ": " + problem.chatSummary()));
			ownerPlayer.sendSystemMessage(Component.literal("Tip: " + ProblemTips.tipFor(problem)));
			if (mobProblems.isEmpty()) {
				ownerPlayer.sendSystemMessage(Component.literal(
						"All demo smells cleared! Score " + scorePoints + ". Use /codearena end for emerald reward."));
			}
		}
	}

	public void listProblems(ServerPlayer player) {
		if (!active) {
			player.sendSystemMessage(Component.literal("No active CodeArena session."));
			return;
		}
		player.sendSystemMessage(Component.literal(
				"=== CodeArena demo problems (" + buildResult.mode() + " / "
						+ String.join("+", buildResult.placedStructures()) + ") ==="));
		for (CodeProblem problem : problems) {
			boolean alive = mobProblems.containsValue(problem);
			player.sendSystemMessage(Component.literal(
					(alive ? "[ ] " : "[x] ") + problem.chatSummary()));
		}
		player.sendSystemMessage(Component.literal(
				"Score: " + scorePoints + " | Remaining mobs: " + mobProblems.size()
						+ " | Filter: " + currentFilter));
	}

	public void end() {
		if (!active) {
			return;
		}
		active = false;
		for (UUID id : spawnedMobs) {
			Entity e = level.getEntity(id);
			if (e != null) {
				e.discard();
			}
		}
		spawnedMobs.clear();
		mobProblems.clear();

		level.getGameRules().set(GameRules.MOB_GRIEFING, previousMobGriefing, level.getServer());

		if (scoreObjective != null) {
			Scoreboard board = level.getScoreboard();
			if (board.getDisplayObjective(DisplaySlot.SIDEBAR) == scoreObjective) {
				board.setDisplayObjective(DisplaySlot.SIDEBAR, null);
			}
			board.removeObjective(scoreObjective);
			scoreObjective = null;
		}

		ServerPlayer ownerPlayer = level.getServer().getPlayerList().getPlayer(owner);
		if (ownerPlayer != null) {
			int emeralds = Math.min(64, Math.max(0, scorePoints));
			if (emeralds > 0) {
				ownerPlayer.getInventory().add(new ItemStack(Items.EMERALD, emeralds));
			}
			ownerPlayer.sendSystemMessage(Component.literal(
					"CodeArena session ended. Score " + scorePoints + ", reward " + emeralds + " emerald(s)."));
		}
	}

	public boolean isActive() {
		return active;
	}

	public BlockPos getCenter() {
		return center;
	}

	public ArenaBuilder.BuildMode getBuildMode() {
		return buildResult.mode();
	}

	public int getScorePoints() {
		return scorePoints;
	}
}
