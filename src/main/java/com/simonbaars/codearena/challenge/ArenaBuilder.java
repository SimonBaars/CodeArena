package com.simonbaars.codearena.challenge;

import com.simonbaars.codearena.CodeArenaMod;
import com.simonbaars.codearena.structureloader.SchematicStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Prefers legacy {@code structures/arena.structure} (1.12 schematic with remapped block ids).
 * Falls back to a procedural sandstone coliseum if the schematic cannot be loaded.
 */
public final class ArenaBuilder {
	public static final int FLOOR_HALF_X = 15;
	public static final int FLOOR_HALF_Z = 20;
	public static final int WALL_HEIGHT = 5;

	public enum BuildMode {
		SCHEMATIC,
		PROCEDURAL
	}

	private ArenaBuilder() {}

	public static BuildMode build(ServerLevel level, BlockPos center) {
		SchematicStructure schematic = new SchematicStructure("arena");
		if (schematic.readFromClasspath()) {
			int placed = schematic.placeCentered(level, center);
			if (placed > 0) {
				// Center marker so the player spawn spot stays obvious
				level.setBlock(center.above(), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
				CodeArenaMod.LOGGER.info("Arena built from legacy arena.structure ({} blocks)", placed);
				return BuildMode.SCHEMATIC;
			}
		}
		CodeArenaMod.LOGGER.warn("Schematic load failed — using procedural sandstone arena");
		buildProcedural(level, center);
		return BuildMode.PROCEDURAL;
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
