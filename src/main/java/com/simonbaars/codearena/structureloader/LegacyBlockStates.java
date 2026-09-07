package com.simonbaars.codearena.structureloader;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * Converts pre-flattening (≈1.12) schematic {@code Blocks}/{@code Data} pairs into modern
 * {@link BlockState}s. Legacy CodeArena (and IMS) used {@code Block#getStateFromMeta}; the Fabric port previously
 * placed {@code defaultBlockState()} only, dropping facing/axis/color/slab half.
 */
public final class LegacyBlockStates {
	private static final String[] DYE = {
		"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
		"light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
	};
	private static final String[] WOOD = {
		"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"
	};
	/** Stone slab (44/43) variants. */
	private static final String[] STONE_SLAB = {
		"smooth_stone_slab", "sandstone_slab", "petrified_oak_slab", "cobblestone_slab",
		"brick_slab", "stone_brick_slab", "nether_brick_slab", "quartz_slab"
	};
	/** Legacy stair block IDs that use the standard stair meta encoding. */
	private static final int[] STAIR_IDS = {
		53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180, 203
	};

	private LegacyBlockStates() {}

	public static BlockState fromLegacy(int id, int meta) {
		meta &= 0xF;
		BlockState state = mapKnown(id, meta);
		if (state == null) {
			Block block = blockByLegacyIdOnly(id);
			if (block == null) {
				return null;
			}
			state = block.defaultBlockState();
		}
		return applyGenericOrientation(id, meta, state);
	}

	private static BlockState mapKnown(int id, int meta) {
		return switch (id) {
			case 0 -> Blocks.AIR.defaultBlockState();
			case 1 -> stone(meta);
			case 3 -> dirt(meta);
			case 5 -> planks(meta);
			case 6 -> sapling(meta);
			case 17 -> log(meta, false);
			case 18 -> leaves(meta, false);
			case 19 -> meta == 1 ? block("wet_sponge") : Blocks.SPONGE.defaultBlockState();
			case 24 -> sandstone(meta, false);
			case 35 -> colored("wool", meta);
			case 43 -> stoneSlab(meta, true);
			case 44 -> stoneSlab(meta, false);
			case 95 -> colored("stained_glass", meta);
			case 98 -> stoneBricks(meta);
			case 125 -> woodSlab(meta, true);
			case 126 -> woodSlab(meta, false);
			case 155 -> quartz(meta);
			case 159 -> colored("terracotta", meta);
			case 160 -> colored("stained_glass_pane", meta);
			case 161 -> leaves(meta, true);
			case 162 -> log(meta, true);
			case 168 -> prismarine(meta);
			case 171 -> colored("carpet", meta);
			case 175 -> tallFlower(meta);
			case 179 -> sandstone(meta, true);
			case 204 -> colored("concrete", meta);
			case 205 -> colored("concrete_powder", meta);
			default -> null;
		};
	}

	private static BlockState applyGenericOrientation(int id, int meta, BlockState state) {
		if (state == null || state.isAir()) {
			return state;
		}

		// Stairs: facing 0=E 1=W 2=S 3=N; bit4 = upside-down
		if (isStairId(id) || state.getBlock() instanceof StairBlock) {
			Direction facing = switch (meta & 3) {
				case 0 -> Direction.EAST;
				case 1 -> Direction.WEST;
				case 2 -> Direction.SOUTH;
				default -> Direction.NORTH;
			};
			Half half = (meta & 4) != 0 ? Half.TOP : Half.BOTTOM;
			if (state.hasProperty(StairBlock.FACING)) {
				state = state.setValue(StairBlock.FACING, facing);
			}
			if (state.hasProperty(StairBlock.HALF)) {
				state = state.setValue(StairBlock.HALF, half);
			}
			return state;
		}

		// Slabs: bit 0x8 = top half (single). Double handled in mapKnown.
		if (state.getBlock() instanceof SlabBlock && state.hasProperty(SlabBlock.TYPE)) {
			if (state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE) {
				state = state.setValue(SlabBlock.TYPE, (meta & 8) != 0 ? SlabType.TOP : SlabType.BOTTOM);
			}
			return state;
		}

		// Pillar / log axis already applied in mapKnown for 17/162/155/170/216; still apply AXIS if present
		if (state.getBlock() instanceof RotatedPillarBlock && state.hasProperty(RotatedPillarBlock.AXIS)
				&& (id == 170 || id == 216 || id == 202)) {
			Direction.Axis axis = switch ((meta >> 2) & 3) {
				case 1 -> Direction.Axis.X;
				case 2 -> Direction.Axis.Z;
				default -> Direction.Axis.Y;
			};
			return state.setValue(RotatedPillarBlock.AXIS, axis);
		}

		// Ladder
		if (id == 65 || state.getBlock() instanceof LadderBlock) {
			Direction facing = facingNESW(meta);
			if (state.hasProperty(LadderBlock.FACING)) {
				return state.setValue(LadderBlock.FACING, facing);
			}
		}

		// Furnace / lit furnace / chest / trapped chest / ender chest / dispenser / dropper / hopper
		if (id == 61 || id == 62 || id == 54 || id == 146 || id == 130 || id == 23 || id == 158 || id == 154) {
			if (id == 154 && state.hasProperty(BlockStateProperties.FACING_HOPPER)) {
				Direction f = meta == 0 ? Direction.DOWN : facingNESW(meta);
				if (meta == 1) {
					f = Direction.UP; // unused historically but keep safe
				}
				return state.setValue(BlockStateProperties.FACING_HOPPER, f == Direction.UP ? Direction.DOWN : f);
			}
			if (state.hasProperty(BlockStateProperties.FACING)) {
				return state.setValue(BlockStateProperties.FACING, facingFull(meta));
			}
			if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
				return state.setValue(BlockStateProperties.HORIZONTAL_FACING, facingNESW(meta));
			}
		}

		// Torch / redstone torch wall vs floor
		if (id == 50 || id == 75 || id == 76) {
			if (meta >= 1 && meta <= 4 && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
				Direction facing = switch (meta) {
					case 1 -> Direction.EAST;
					case 2 -> Direction.WEST;
					case 3 -> Direction.SOUTH;
					default -> Direction.NORTH;
				};
				// Wall torch is a different block in modern MC
				String wall = id == 50 ? "wall_torch" : (id == 75 ? "redstone_wall_torch" : "redstone_wall_torch");
				BlockState wallState = block(wall);
				if (wallState != null && wallState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
					wallState = wallState.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
					if (id == 75 && wallState.hasProperty(BlockStateProperties.LIT)) {
						wallState = wallState.setValue(BlockStateProperties.LIT, false);
					}
					return wallState;
				}
			}
			if (id == 75 && state.hasProperty(BlockStateProperties.LIT)) {
				return state.setValue(BlockStateProperties.LIT, false);
			}
			return state;
		}

		// Pumpkin / jack o lantern / carved
		if (id == 86 || id == 91) {
			if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
				return state.setValue(BlockStateProperties.HORIZONTAL_FACING, facingCardinal(meta & 3));
			}
		}

		// Fence gate / trapdoor-ish horizontal
		if (id == 107 || id == 183 || id == 184 || id == 185 || id == 186 || id == 187) {
			if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
				state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facingCardinal(meta & 3));
			}
			if (state.hasProperty(BlockStateProperties.OPEN)) {
				state = state.setValue(BlockStateProperties.OPEN, (meta & 4) != 0);
			}
			return state;
		}

		// Wooden / iron trapdoor (96 / 167)
		if (id == 96 || id == 167) {
			Direction facing = switch (meta & 3) {
				case 0 -> Direction.NORTH;
				case 1 -> Direction.SOUTH;
				case 2 -> Direction.WEST;
				default -> Direction.EAST;
			};
			if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
				state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
			}
			if (state.hasProperty(BlockStateProperties.OPEN)) {
				state = state.setValue(BlockStateProperties.OPEN, (meta & 4) != 0);
			}
			if (state.hasProperty(BlockStateProperties.HALF)) {
				state = state.setValue(BlockStateProperties.HALF, (meta & 8) != 0 ? Half.TOP : Half.BOTTOM);
			}
			return state;
		}

		// Button (77 stone / 143 wood): facing + powered
		if (id == 77 || id == 143) {
			Direction facing = switch (meta & 7) {
				case 1 -> Direction.EAST;
				case 2 -> Direction.WEST;
				case 3 -> Direction.SOUTH;
				case 4 -> Direction.NORTH;
				case 5 -> Direction.UP;
				default -> Direction.DOWN;
			};
			if (state.hasProperty(BlockStateProperties.ATTACH_FACE)) {
				AttachFace face = facing.getAxis().isVertical()
					? (facing == Direction.UP ? AttachFace.FLOOR : AttachFace.CEILING)
					: AttachFace.WALL;
				state = state.setValue(BlockStateProperties.ATTACH_FACE, face);
			}
			if (facing.getAxis().isHorizontal() && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
				state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
			}
			if (state.hasProperty(BlockStateProperties.POWERED)) {
				state = state.setValue(BlockStateProperties.POWERED, (meta & 8) != 0);
			}
			return state;
		}

		// Lever (69)
		if (id == 69) {
			int facingMeta = meta & 7;
			Direction facing;
			AttachFace face;
			switch (facingMeta) {
				case 1 -> { facing = Direction.EAST; face = AttachFace.WALL; }
				case 2 -> { facing = Direction.WEST; face = AttachFace.WALL; }
				case 3 -> { facing = Direction.SOUTH; face = AttachFace.WALL; }
				case 4 -> { facing = Direction.NORTH; face = AttachFace.WALL; }
				case 5, 6 -> { facing = Direction.NORTH; face = AttachFace.FLOOR; }
				default -> { facing = Direction.NORTH; face = AttachFace.CEILING; }
			}
			if (state.hasProperty(BlockStateProperties.ATTACH_FACE)) {
				state = state.setValue(BlockStateProperties.ATTACH_FACE, face);
			}
			if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
				state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
			}
			if (state.hasProperty(BlockStateProperties.POWERED)) {
				state = state.setValue(BlockStateProperties.POWERED, (meta & 8) != 0);
			}
			return state;
		}

		// Piston / sticky (33/29) + head (34)
		if (id == 33 || id == 29 || id == 34) {
			if (state.hasProperty(BlockStateProperties.FACING)) {
				state = state.setValue(BlockStateProperties.FACING, facingFull(meta & 7));
			}
			if (id != 34 && state.hasProperty(BlockStateProperties.EXTENDED)) {
				state = state.setValue(BlockStateProperties.EXTENDED, (meta & 8) != 0);
			}
			return state;
		}

		// Rails (66 normal, 27/28/157 powered variants)
		if (id == 66 || id == 27 || id == 28 || id == 157) {
			if (state.hasProperty(BlockStateProperties.RAIL_SHAPE)) {
				RailShape shape = railShape(meta);
				if (shape != null && state.getValue(BlockStateProperties.RAIL_SHAPE).ordinal() >= 0) {
					try {
						state = state.setValue(BlockStateProperties.RAIL_SHAPE, shape);
					} catch (IllegalArgumentException ignored) {
						// powered rails reject curves
					}
				}
			} else if (state.hasProperty(BlockStateProperties.RAIL_SHAPE_STRAIGHT)) {
				RailShape shape = railShapeStraight(meta);
				if (shape != null) {
					try {
						state = state.setValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT, shape);
					} catch (IllegalArgumentException ignored) {
					}
				}
			}
			if (state.hasProperty(BlockStateProperties.POWERED) && (id == 27 || id == 28 || id == 157)) {
				state = state.setValue(BlockStateProperties.POWERED, (meta & 8) != 0);
			}
			return state;
		}

		// Anvil (145): facing + damage → chipped/damaged blocks
		if (id == 145) {
			int dmg = (meta >> 2) & 3;
			String name = dmg == 1 ? "chipped_anvil" : dmg >= 2 ? "damaged_anvil" : "anvil";
			BlockState anvil = block(name);
			if (anvil != null) {
				state = anvil;
			}
			if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
				state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facingCardinal(meta & 3));
			}
			return state;
		}

		// Glazed terracotta 235-250: facing
		if (id >= 235 && id <= 250 && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			return state.setValue(BlockStateProperties.HORIZONTAL_FACING, facingCardinal(meta & 3));
		}

		// Observer (218)
		if (id == 218 && state.hasProperty(BlockStateProperties.FACING)) {
			return state.setValue(BlockStateProperties.FACING, facingFull(meta & 7));
		}

		// End rod (198)
		if (id == 198 && state.hasProperty(BlockStateProperties.FACING)) {
			return state.setValue(BlockStateProperties.FACING, facingFull(meta & 7));
		}

		// Generic horizontal facing fallback (signs, banners, etc.)
		if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
				&& !state.hasProperty(BlockStateProperties.ATTACH_FACE)) {
			// Only apply when meta looks like NESW (2-5) furnace-style OR 0-3 cardinal
			if (meta >= 2 && meta <= 5) {
				return state.setValue(BlockStateProperties.HORIZONTAL_FACING, facingNESW(meta));
			}
		}

		return state;
	}

	private static boolean isStairId(int id) {
		for (int s : STAIR_IDS) {
			if (s == id) return true;
		}
		return false;
	}

	private static Direction facingNESW(int meta) {
		return switch (meta) {
			case 3 -> Direction.SOUTH;
			case 4 -> Direction.WEST;
			case 5 -> Direction.EAST;
			default -> Direction.NORTH;
		};
	}

	private static Direction facingFull(int meta) {
		return switch (meta) {
			case 0 -> Direction.DOWN;
			case 1 -> Direction.UP;
			case 2 -> Direction.NORTH;
			case 3 -> Direction.SOUTH;
			case 4 -> Direction.WEST;
			case 5 -> Direction.EAST;
			default -> Direction.NORTH;
		};
	}

	private static Direction facingCardinal(int meta) {
		return switch (meta & 3) {
			case 1 -> Direction.WEST;
			case 2 -> Direction.NORTH;
			case 3 -> Direction.EAST;
			default -> Direction.SOUTH;
		};
	}

	private static RailShape railShape(int meta) {
		return switch (meta & 15) {
			case 0 -> RailShape.NORTH_SOUTH;
			case 1 -> RailShape.EAST_WEST;
			case 2 -> RailShape.ASCENDING_EAST;
			case 3 -> RailShape.ASCENDING_WEST;
			case 4 -> RailShape.ASCENDING_NORTH;
			case 5 -> RailShape.ASCENDING_SOUTH;
			case 6 -> RailShape.SOUTH_EAST;
			case 7 -> RailShape.SOUTH_WEST;
			case 8 -> RailShape.NORTH_WEST;
			case 9 -> RailShape.NORTH_EAST;
			default -> null;
		};
	}

	private static RailShape railShapeStraight(int meta) {
		return switch (meta & 7) {
			case 0 -> RailShape.NORTH_SOUTH;
			case 1 -> RailShape.EAST_WEST;
			case 2 -> RailShape.ASCENDING_EAST;
			case 3 -> RailShape.ASCENDING_WEST;
			case 4 -> RailShape.ASCENDING_NORTH;
			case 5 -> RailShape.ASCENDING_SOUTH;
			default -> null;
		};
	}

	private static BlockState stone(int meta) {
		return switch (meta) {
			case 1 -> block("granite");
			case 2 -> block("polished_granite");
			case 3 -> block("diorite");
			case 4 -> block("polished_diorite");
			case 5 -> block("andesite");
			case 6 -> block("polished_andesite");
			default -> Blocks.STONE.defaultBlockState();
		};
	}

	private static BlockState dirt(int meta) {
		return switch (meta) {
			case 1 -> block("coarse_dirt");
			case 2 -> block("podzol");
			default -> Blocks.DIRT.defaultBlockState();
		};
	}

	private static BlockState planks(int meta) {
		String wood = WOOD[Math.min(meta & 7, WOOD.length - 1)];
		return block(wood + "_planks");
	}

	private static BlockState sapling(int meta) {
		String wood = WOOD[Math.min(meta & 7, WOOD.length - 1)];
		return block(wood + "_sapling");
	}

	private static BlockState log(int meta, boolean newLog) {
		int woodIdx = meta & 3;
		String wood;
		if (newLog) {
			wood = woodIdx == 0 ? "acacia" : "dark_oak";
		} else {
			wood = WOOD[Math.min(woodIdx, 3)];
		}
		int axisBits = (meta >> 2) & 3;
		if (axisBits == 3) {
			BlockState woodBlock = block(wood + "_wood");
			return woodBlock != null ? woodBlock : block(wood + "_log");
		}
		BlockState log = block(wood + "_log");
		if (log == null) return null;
		Direction.Axis axis = switch (axisBits) {
			case 1 -> Direction.Axis.X;
			case 2 -> Direction.Axis.Z;
			default -> Direction.Axis.Y;
		};
		if (log.hasProperty(RotatedPillarBlock.AXIS)) {
			log = log.setValue(RotatedPillarBlock.AXIS, axis);
		}
		return log;
	}

	private static BlockState leaves(int meta, boolean newLeaves) {
		int woodIdx = meta & 3;
		String wood;
		if (newLeaves) {
			wood = woodIdx == 0 ? "acacia" : "dark_oak";
		} else {
			wood = WOOD[Math.min(woodIdx, 3)];
		}
		BlockState leaves = block(wood + "_leaves");
		if (leaves != null && leaves.hasProperty(BlockStateProperties.PERSISTENT)) {
			// bit 4 in legacy = no-decay check; treat as persistent when set
			leaves = leaves.setValue(BlockStateProperties.PERSISTENT, (meta & 4) != 0);
		}
		return leaves;
	}

	private static BlockState sandstone(int meta, boolean red) {
		String base = red ? "red_sandstone" : "sandstone";
		return switch (meta) {
			case 1 -> block("chiseled_" + base);
			case 2 -> block("cut_" + base);
			default -> block(base);
		};
	}

	private static BlockState stoneBricks(int meta) {
		return switch (meta) {
			case 1 -> block("mossy_stone_bricks");
			case 2 -> block("cracked_stone_bricks");
			case 3 -> block("chiseled_stone_bricks");
			default -> Blocks.STONE_BRICKS.defaultBlockState();
		};
	}

	private static BlockState quartz(int meta) {
		return switch (meta) {
			case 1 -> block("chiseled_quartz_block");
			case 2 -> pillarAxis(block("quartz_pillar"), Direction.Axis.Y);
			case 3 -> pillarAxis(block("quartz_pillar"), Direction.Axis.X);
			case 4 -> pillarAxis(block("quartz_pillar"), Direction.Axis.Z);
			default -> Blocks.QUARTZ_BLOCK.defaultBlockState();
		};
	}

	private static BlockState pillarAxis(BlockState state, Direction.Axis axis) {
		if (state != null && state.hasProperty(RotatedPillarBlock.AXIS)) {
			return state.setValue(RotatedPillarBlock.AXIS, axis);
		}
		return state;
	}

	private static BlockState prismarine(int meta) {
		return switch (meta) {
			case 1 -> block("prismarine_bricks");
			case 2 -> block("dark_prismarine");
			default -> Blocks.PRISMARINE.defaultBlockState();
		};
	}

	private static BlockState tallFlower(int meta) {
		// lower half variants; upper half is meta 8+
		boolean upper = (meta & 8) != 0;
		String name = switch (meta & 7) {
			case 1 -> "lilac";
			case 2 -> "tall_grass";
			case 3 -> "large_fern";
			case 4 -> "rose_bush";
			case 5 -> "peony";
			default -> "sunflower";
		};
		BlockState state = block(name);
		if (state != null && state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
			state = state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF,
				upper ? net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER
					: net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER);
		}
		return state;
	}

	private static BlockState stoneSlab(int meta, boolean doubleSlab) {
		String name = STONE_SLAB[Math.min(meta & 7, STONE_SLAB.length - 1)];
		BlockState state = block(name);
		if (state == null) return null;
		if (state.hasProperty(SlabBlock.TYPE)) {
			if (doubleSlab) {
				state = state.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
			} else {
				state = state.setValue(SlabBlock.TYPE, (meta & 8) != 0 ? SlabType.TOP : SlabType.BOTTOM);
			}
		}
		return state;
	}

	private static BlockState woodSlab(int meta, boolean doubleSlab) {
		String wood = WOOD[Math.min(meta & 7, WOOD.length - 1)];
		BlockState state = block(wood + "_slab");
		if (state == null) return null;
		if (state.hasProperty(SlabBlock.TYPE)) {
			if (doubleSlab) {
				state = state.setValue(SlabBlock.TYPE, SlabType.DOUBLE);
			} else {
				state = state.setValue(SlabBlock.TYPE, (meta & 8) != 0 ? SlabType.TOP : SlabType.BOTTOM);
			}
		}
		return state;
	}

	private static BlockState colored(String suffix, int meta) {
		String color = DYE[Math.min(meta & 15, DYE.length - 1)];
		return block(color + "_" + suffix);
	}

	/**
	 * Fallback ID→block for IDs not fully handled in {@link #mapKnown}. Mirrors the existing
	 * SchematicStructure table so unmapped orientation-only blocks still resolve.
	 */
	private static Block blockByLegacyIdOnly(int legacyId) {
		String[] legacyMappings = {
			"air", "stone", "grass_block", "dirt", "cobblestone", "oak_planks",
			"oak_sapling", "bedrock", "water", "water", "lava", "lava", "sand",
			"gravel", "gold_ore", "iron_ore", "coal_ore", "oak_log", "oak_leaves",
			"sponge", "glass", "lapis_ore", "lapis_block", "dispenser", "sandstone",
			"note_block", "red_bed", "powered_rail", "detector_rail", "sticky_piston",
			"cobweb", "short_grass", "dead_bush", "piston", "white_wool", "dandelion",
			"poppy", "brown_mushroom", "red_mushroom", "gold_block", "iron_block",
			"smooth_stone_slab", "bricks", "tnt", "bookshelf", "mossy_cobblestone",
			"obsidian", "torch", "fire", "spawner", "oak_stairs", "chest", "redstone_wire",
			"diamond_ore", "diamond_block", "crafting_table", "wheat", "farmland",
			"furnace", "oak_sign", "oak_door", "ladder", "rail", "cobblestone_stairs",
			"oak_wall_sign", "lever", "stone_pressure_plate", "iron_door", "oak_pressure_plate",
			"redstone_ore", "redstone_torch", "stone_button", "snow", "ice", "snow_block",
			"cactus", "clay", "sugar_cane", "jukebox", "oak_fence", "pumpkin",
			"netherrack", "soul_sand", "glowstone", "nether_portal", "jack_o_lantern",
			"cake", "repeater", "white_stained_glass", "oak_trapdoor", "stone_bricks",
			"brown_mushroom_block", "red_mushroom_block", "iron_bars", "glass_pane",
			"melon", "pumpkin_stem", "melon_stem", "vine", "oak_fence_gate",
			"brick_stairs", "stone_brick_stairs", "mycelium", "lily_pad", "nether_bricks",
			"nether_brick_fence", "nether_brick_stairs", "nether_wart", "enchanting_table",
			"brewing_stand", "cauldron", "end_portal", "end_portal_frame", "end_stone",
			"dragon_egg", "redstone_lamp", "oak_slab", "sandstone_stairs", "emerald_ore",
			"ender_chest", "tripwire_hook", "tripwire", "emerald_block", "spruce_stairs",
			"birch_stairs", "jungle_stairs", "command_block", "beacon", "cobblestone_wall",
			"flower_pot", "carrots", "potatoes", "oak_button", "skeleton_skull",
			"anvil", "trapped_chest", "light_weighted_pressure_plate", "heavy_weighted_pressure_plate",
			"comparator", "daylight_detector", "redstone_block", "nether_quartz_ore", "hopper",
			"quartz_block", "quartz_stairs", "activator_rail", "dropper", "white_terracotta",
			"white_stained_glass_pane", "acacia_leaves", "acacia_log", "acacia_stairs", "dark_oak_stairs",
			"slime_block", "barrier", "iron_trapdoor", "prismarine", "sea_lantern",
			"hay_block", "white_carpet", "terracotta", "coal_block", "packed_ice",
			"sunflower", "white_banner", "white_wall_banner", "daylight_detector", "red_sandstone",
			"red_sandstone_stairs", "red_sandstone_slab", "spruce_fence_gate", "birch_fence_gate",
			"jungle_fence_gate", "dark_oak_fence_gate", "acacia_fence_gate", "spruce_fence",
			"birch_fence", "jungle_fence", "dark_oak_fence", "acacia_fence", "spruce_door",
			"birch_door", "jungle_door", "acacia_door", "dark_oak_door", "end_rod",
			"chorus_plant", "chorus_flower", "purpur_block", "purpur_pillar", "purpur_stairs",
			"purpur_slab", "end_stone_bricks", "beetroots", "dirt_path", "end_gateway",
			"repeating_command_block", "chain_command_block", "frosted_ice", "magma_block",
			"nether_wart_block", "red_nether_bricks", "bone_block", "structure_void",
			"observer", "white_shulker_box", "orange_shulker_box"
		};
		if (legacyId < 0 || legacyId >= legacyMappings.length) {
			return null;
		}
		return BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("minecraft", legacyMappings[legacyId]))
			.map(h -> h.value())
			.orElse(null);
	}

	private static BlockState block(String name) {
		return BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath("minecraft", name))
			.map(h -> h.value().defaultBlockState())
			.orElse(null);
	}
}
