package com.simonbaars.codearena.structureloader;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Forge 1.12 numeric block ids → Minecraft 26.2 blocks.
 * Explicit cases cover every id used by {@code structures/arena.structure}
 * (0,1,12,44,50,67,69,71,85,89,197) plus common arena/coliseum extras.
 */
public final class LegacyBlockIds {
	private LegacyBlockIds() {}

	public static Block fromId(int id) {
		return switch (id) {
			case 0 -> Blocks.AIR;
			case 1 -> Blocks.STONE;
			case 2 -> Blocks.GRASS_BLOCK;
			case 3 -> Blocks.DIRT;
			case 4 -> Blocks.COBBLESTONE;
			case 5 -> Blocks.OAK_PLANKS;
			case 7 -> Blocks.BEDROCK;
			case 8, 9 -> Blocks.WATER;
			case 10, 11 -> Blocks.LAVA;
			case 12 -> Blocks.SAND;
			case 13 -> Blocks.GRAVEL;
			case 17 -> Blocks.OAK_LOG;
			case 18 -> Blocks.OAK_LEAVES;
			case 20 -> Blocks.GLASS;
			case 24 -> Blocks.SANDSTONE;
			case 35 -> Blocks.WOOL.white();
			case 41 -> Blocks.GOLD_BLOCK;
			case 42 -> Blocks.IRON_BLOCK;
			case 44 -> Blocks.SMOOTH_STONE_SLAB;
			case 45 -> Blocks.BRICKS;
			case 48 -> Blocks.MOSSY_COBBLESTONE;
			case 49 -> Blocks.OBSIDIAN;
			case 50 -> Blocks.TORCH;
			case 53 -> Blocks.OAK_STAIRS;
			case 64 -> Blocks.OAK_DOOR;
			case 65 -> Blocks.LADDER;
			case 67 -> Blocks.COBBLESTONE_STAIRS;
			case 69 -> Blocks.LEVER;
			case 71 -> Blocks.IRON_DOOR;
			case 85 -> Blocks.OAK_FENCE;
			case 89 -> Blocks.GLOWSTONE;
			case 98 -> Blocks.STONE_BRICKS;
			case 112 -> Blocks.NETHER_BRICKS;
			case 133 -> Blocks.EMERALD_BLOCK;
			case 139 -> Blocks.COBBLESTONE_WALL;
			case 155 -> Blocks.QUARTZ_BLOCK;
			case 159 -> Blocks.DYED_TERRACOTTA.white();
			case 197 -> Blocks.DARK_OAK_FENCE_GATE;
			default -> Blocks.STONE;
		};
	}
}
