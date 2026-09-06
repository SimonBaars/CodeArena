package com.simonbaars.codearena.challenge;

import com.simonbaars.codearena.CodeArenaMod;
import com.simonbaars.codearena.structureloader.SchematicStructure;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Prefers legacy {@code structures/arena.structure}, places corner {@code watchtower}s when present,
 * and can place optional large schematics ({@code coliseum}/{@code colloseum}) on demand.
 */
public final class ArenaBuilder {
	public static final int FLOOR_HALF_X = 15;
	public static final int FLOOR_HALF_Z = 20;
	public static final int WALL_HEIGHT = 5;

	public enum BuildMode {
		SCHEMATIC,
		PROCEDURAL
	}

	public record BuildResult(BuildMode mode, List<String> placedStructures, int totalBlocks) {}

	private ArenaBuilder() {}

	public static BuildResult build(ServerLevel level, BlockPos center) {
		List<String> placed = new ArrayList<>();
		int total = 0;

		SchematicStructure arena = new SchematicStructure("arena");
		BuildMode mode;
		if (arena.readFromClasspath()) {
			int n = arena.placeCentered(level, center);
			if (n > 0) {
				placed.add("arena");
				total += n;
				mode = BuildMode.SCHEMATIC;
				CodeArenaMod.LOGGER.info("Arena built from legacy arena.structure ({} blocks)", n);
			} else {
				CodeArenaMod.LOGGER.warn("Arena schematic empty — procedural fallback");
				buildProcedural(level, center);
				placed.add("procedural");
				mode = BuildMode.PROCEDURAL;
			}
		} else {
			CodeArenaMod.LOGGER.warn("Schematic load failed — using procedural sandstone arena");
			buildProcedural(level, center);
			placed.add("procedural");
			mode = BuildMode.PROCEDURAL;
		}

		// Corner watchtowers (small 13x30x13) — skip if schematic missing
		SchematicStructure tower = new SchematicStructure("watchtower");
		if (tower.readFromClasspath()) {
			int halfX = Math.max(18, arena.isLoaded() ? arena.getLength() / 2 + 2 : FLOOR_HALF_X + 3);
			int halfZ = Math.max(22, arena.isLoaded() ? arena.getWidth() / 2 + 2 : FLOOR_HALF_Z + 3);
			int[][] corners = {
					{-halfX, -halfZ},
					{halfX - tower.getLength(), -halfZ},
					{-halfX, halfZ - tower.getWidth()},
					{halfX - tower.getLength(), halfZ - tower.getWidth()}
			};
			int towers = 0;
			for (int[] c : corners) {
				int n = tower.placeAt(level, center.getX() + c[0], center.getY(), center.getZ() + c[1]);
				if (n > 0) {
					towers++;
					total += n;
				}
			}
			if (towers > 0) {
				placed.add("watchtowerx" + towers);
				CodeArenaMod.LOGGER.info("Placed {} watchtowers around arena", towers);
			}
		}

		level.setBlock(center.above(), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
		return new BuildResult(mode, List.copyOf(placed), total);
	}

	/**
	 * Places a named schematic centered on {@code center}. Large coliseum schematics are allowed
	 * but may hitch the server briefly.
	 */
	public static int placeNamed(ServerLevel level, BlockPos center, String structureName) {
		String key = structureName.toLowerCase(Locale.ROOT).trim();
		SchematicStructure schematic = new SchematicStructure(key);
		if (!schematic.readFromClasspath()) {
			return -1;
		}
		int placed = schematic.placeCentered(level, center);
		if (placed > 0) {
			level.setBlock(center.above(), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
		}
		return placed;
	}

	public static void buildProcedural(ServerLevel level, BlockPos center) {
		int cy = center.getY();
		int cx = center.getX();
		int cz = center.getZ();

		BlockState floor = Blocks.SANDSTONE.defaultBlockState();
		BlockState wall = Blocks.SMOOTH_SANDSTONE.defaultBlockState();
		BlockState pillar = Blocks.CUT_SANDSTONE.defaultBlockState();
		BlockState air = Blocks.AIR.defaultBlockState();

		for (int x = -FLOOR_HALF_X; x <= FLOOR_HALF_X; x++) {
			for (int z = -FLOOR_HALF_Z; z <= FLOOR_HALF_Z; z++) {
				level.setBlock(new BlockPos(cx + x, cy, cz + z), floor, 3);
				for (int y = 1; y <= WALL_HEIGHT + 2; y++) {
					level.setBlock(new BlockPos(cx + x, cy + y, cz + z), air, 3);
				}
			}
		}

		for (int x = -FLOOR_HALF_X; x <= FLOOR_HALF_X; x++) {
			for (int y = 1; y <= WALL_HEIGHT; y++) {
				level.setBlock(new BlockPos(cx + x, cy + y, cz - FLOOR_HALF_Z), wall, 3);
				level.setBlock(new BlockPos(cx + x, cy + y, cz + FLOOR_HALF_Z), wall, 3);
			}
		}
		for (int z = -FLOOR_HALF_Z; z <= FLOOR_HALF_Z; z++) {
			for (int y = 1; y <= WALL_HEIGHT; y++) {
				level.setBlock(new BlockPos(cx - FLOOR_HALF_X, cy + y, cz + z), wall, 3);
				level.setBlock(new BlockPos(cx + FLOOR_HALF_X, cy + y, cz + z), wall, 3);
			}
		}

		int[][] corners = {
				{-FLOOR_HALF_X + 2, -FLOOR_HALF_Z + 2},
				{FLOOR_HALF_X - 2, -FLOOR_HALF_Z + 2},
				{-FLOOR_HALF_X + 2, FLOOR_HALF_Z - 2},
				{FLOOR_HALF_X - 2, FLOOR_HALF_Z - 2}
		};
		for (int[] c : corners) {
			for (int y = 1; y <= WALL_HEIGHT + 1; y++) {
				level.setBlock(new BlockPos(cx + c[0], cy + y, cz + c[1]), pillar, 3);
			}
		}

		for (int y = 1; y <= 3; y++) {
			level.setBlock(new BlockPos(cx, cy + y, cz + FLOOR_HALF_Z), air, 3);
			level.setBlock(new BlockPos(cx - 1, cy + y, cz + FLOOR_HALF_Z), air, 3);
			level.setBlock(new BlockPos(cx + 1, cy + y, cz + FLOOR_HALF_Z), air, 3);
		}

		level.setBlock(new BlockPos(cx, cy + 1, cz), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
	}
}
