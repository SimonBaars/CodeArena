package com.simonbaars.codearena.structureloader;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Forge 1.12 numeric block ids → Minecraft 26.2 blocks.
 * Covers arena, watchtower, arenacheck, colloseum, and the bulk of coliseum ids.
 * Metadata (facing) is still ignored at place time.
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
			case 15 -> Blocks.IRON_ORE;
			case 16 -> Blocks.COAL_ORE;
			case 17 -> Blocks.OAK_LOG;
			case 18 -> Blocks.OAK_LEAVES;
			case 20 -> Blocks.GLASS;
			case 22 -> Blocks.LAPIS_BLOCK;
			case 24 -> Blocks.SANDSTONE;
			case 29 -> Blocks.STICKY_PISTON;
			case 30 -> Blocks.COBWEB;
			case 31 -> Blocks.SHORT_GRASS;
			case 33 -> Blocks.PISTON;
			case 34 -> Blocks.PISTON_HEAD;
			case 35 -> Blocks.WOOL.white();
			case 37 -> Blocks.DANDELION;
			case 38 -> Blocks.POPPY;
			case 41 -> Blocks.GOLD_BLOCK;
			case 42 -> Blocks.IRON_BLOCK;
			case 43 -> Blocks.SMOOTH_STONE_SLAB; // double stone slab → single as approx
			case 44 -> Blocks.SMOOTH_STONE_SLAB;
			case 45 -> Blocks.BRICKS;
			case 48 -> Blocks.MOSSY_COBBLESTONE;
			case 49 -> Blocks.OBSIDIAN;
			case 50 -> Blocks.TORCH;
			case 52 -> Blocks.SPAWNER;
			case 53 -> Blocks.OAK_STAIRS;
			case 54 -> Blocks.CHEST;
			case 55 -> Blocks.REDSTONE_WIRE;
			case 63 -> Blocks.OAK_SIGN;
			case 64 -> Blocks.OAK_DOOR;
			case 65 -> Blocks.LADDER;
			case 67 -> Blocks.COBBLESTONE_STAIRS;
			case 68 -> Blocks.OAK_WALL_SIGN;
			case 69 -> Blocks.LEVER;
			case 70 -> Blocks.STONE_PRESSURE_PLATE;
			case 71 -> Blocks.IRON_DOOR;
			case 72 -> Blocks.OAK_PRESSURE_PLATE;
			case 76 -> Blocks.REDSTONE_TORCH;
			case 77 -> Blocks.STONE_BUTTON;
			case 80 -> Blocks.SNOW_BLOCK;
			case 82 -> Blocks.CLAY;
			case 85 -> Blocks.OAK_FENCE;
			case 89 -> Blocks.GLOWSTONE;
			case 92 -> Blocks.CAKE;
			case 93, 94 -> Blocks.REPEATER;
			case 98 -> Blocks.STONE_BRICKS;
			case 101 -> Blocks.IRON_BARS;
			case 102 -> Blocks.GLASS_PANE;
			case 106 -> Blocks.VINE;
			case 109 -> Blocks.STONE_BRICK_STAIRS;
			case 112 -> Blocks.NETHER_BRICKS;
			case 113 -> Blocks.NETHER_BRICK_FENCE;
			case 126 -> Blocks.OAK_SLAB;
			case 128 -> Blocks.SANDSTONE_STAIRS;
			case 133 -> Blocks.EMERALD_BLOCK;
			case 139 -> Blocks.COBBLESTONE_WALL;
			case 143 -> Blocks.OAK_BUTTON;
			case 155 -> Blocks.QUARTZ_BLOCK;
			case 156 -> Blocks.QUARTZ_STAIRS;
			case 159 -> Blocks.DYED_TERRACOTTA.white();
			case 197 -> Blocks.DARK_OAK_FENCE_GATE;
			default -> Blocks.STONE;
		};
	}
}
