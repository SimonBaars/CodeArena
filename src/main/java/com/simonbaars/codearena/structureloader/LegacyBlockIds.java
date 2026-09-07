package com.simonbaars.codearena.structureloader;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Forge 1.12 numeric block ids → Minecraft 26.2 blocks (id-only, meta=0).
 * Placement uses {@link LegacyBlockStates#fromLegacy(int, int)}; this class remains as a
 * thin id-only helper that delegates to that mapper.
 */
public final class LegacyBlockIds {
	private LegacyBlockIds() {}

	/**
	 * Resolves a legacy block id with meta 0. Prefer {@link LegacyBlockStates#fromLegacy}
	 * when schematic {@code Data} is available.
	 */
	public static Block fromId(int id) {
		BlockState state = LegacyBlockStates.fromLegacy(id, 0);
		if (state == null || state.isAir()) {
			return id == 0 ? Blocks.AIR : Blocks.STONE;
		}
		return state.getBlock();
	}
}
