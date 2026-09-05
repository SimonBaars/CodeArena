package com.simonbaars.codearena.challenge;

import com.simonbaars.codearena.monster.SmellMobFactory;
import com.simonbaars.codearena.problem.CodeProblem;
import com.simonbaars.codearena.problem.DemoProblems;
import com.simonbaars.codearena.problem.ProblemType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
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
 * Session stand-in for legacy {@code CodeArena}: schematic (or procedural) arena,
 * demo metric problems as distinctly typed smell mobs, sidebar score.
 */
public final class ArenaSession {
	private static final String OBJECTIVE_ID = "codearena_score";

	private final ServerLevel level;
	private final BlockPos center;
	private final UUID owner;
	private final List<UUID> spawnedMobs = new ArrayList<>();
	private final Map<UUID, CodeProblem> mobProblems = new HashMap<>();
	private final List<CodeProblem> problems;
	private final EnumMap<ProblemType, Integer> resolved = new EnumMap<>(ProblemType.class);
	private final ArenaBuilder.BuildMode buildMode;
	private final boolean previousMobGriefing;
	private Objective scoreObjective;
	private ScoreAccess totalScore;
	private int scorePoints;
	private boolean active = true;

	private ArenaSession(ServerLevel level, BlockPos center, UUID owner,
			List<CodeProblem> problems, ArenaBuilder.BuildMode buildMode, boolean previousMobGriefing) {
		this.level = level;
		this.center = center;
		this.owner = owner;
		this.problems = List.copyOf(problems);
		this.buildMode = buildMode;
		this.previousMobGriefing = previousMobGriefing;
		for (ProblemType type : ProblemType.values()) {
			resolved.put(type, 0);
		}
	}

	public static ArenaSession spawn(ServerLevel level, ServerPlayer player) {
		BlockPos center = player.blockPosition();
		boolean prevGriefing = level.getGameRules().get(GameRules.MOB_GRIEFING);
		level.getGameRules().set(GameRules.MOB_GRIEFING, false, level.getServer());

		ArenaBuilder.BuildMode mode = ArenaBuilder.build(level, center);
		player.teleportTo(center.getX() + 0.5, center.getY() + 3.0, center.getZ() + 0.5);
		player.getInventory().add(new ItemStack(Items.DIAMOND_SWORD));

		List<CodeProblem> problems = DemoProblems.sampleWave();
		ArenaSession session = new ArenaSession(level, center, player.getUUID(), problems, mode, prevGriefing);
		session.setupScoreboard();
		session.spawnProblems();

		String source = mode == ArenaBuilder.BuildMode.SCHEMATIC ? "legacy arena.structure" : "procedural sandstone";
		player.sendSystemMessage(Component.literal(
				"CodeArena ready (" + source + "). Defeat " + problems.size()
						+ " metric smells — spider=Duplication, zombie=Complexity, skeleton=Interface, creeper=Volume. "
						+ "Demo problems only (AST clone engine not ported). /codearena problems for details."));
		return session;
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
			board.getOrCreatePlayerScore(ScoreHolder.forNameOnly(type.getDisplayName()), scoreObjective).set(0);
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
			double ox = Math.cos(angle) * 8.0;
			double oz = Math.sin(angle) * 10.0;
			mob.setPos(center.getX() + 0.5 + ox, center.getY() + 2.0, center.getZ() + 0.5 + oz);
			level.addFreshEntity(mob);
			spawnedMobs.add(mob.getUUID());
			mobProblems.put(mob.getUUID(), problem);
		}
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
			board.getOrCreatePlayerScore(ScoreHolder.forNameOnly(problem.getType().getDisplayName()), scoreObjective)
					.set(resolved.get(problem.getType()));
			board.getOrCreatePlayerScore(ScoreHolder.forNameOnly("Remaining"), scoreObjective)
					.set(mobProblems.size());
		}

		ServerPlayer ownerPlayer = level.getServer().getPlayerList().getPlayer(owner);
		if (ownerPlayer != null) {
			ownerPlayer.sendSystemMessage(Component.literal(
					"Resolved +" + points + ": " + problem.chatSummary()));
			ownerPlayer.sendSystemMessage(Component.literal("Tip: " + problem.getTip()));
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
		player.sendSystemMessage(Component.literal("=== CodeArena demo problems (" + buildMode + ") ==="));
		for (CodeProblem problem : problems) {
			boolean alive = mobProblems.containsValue(problem);
			player.sendSystemMessage(Component.literal(
					(alive ? "[ ] " : "[x] ") + problem.chatSummary()));
		}
		player.sendSystemMessage(Component.literal("Score: " + scorePoints + " | Remaining mobs: " + mobProblems.size()));
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
		return buildMode;
	}

	public int getScorePoints() {
		return scorePoints;
	}
}
