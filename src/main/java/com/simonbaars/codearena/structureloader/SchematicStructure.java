package com.simonbaars.codearena.structureloader;

import com.simonbaars.codearena.CodeArenaMod;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Loads legacy gzipped schematic {@code .structure} files (Width/Length/Height + Blocks byte array)
 * using 1.12 numeric ids remapped via {@link LegacyBlockIds}.
 */
public final class SchematicStructure {
	private final String name;
	private final String resourcePath;
	private Block[][][] blocks;
	private int length;
	private int height;
	private int width;
	private boolean loaded;

	public SchematicStructure(String structureName) {
		this.name = structureName;
		this.resourcePath = "/structures/" + structureName + ".structure";
	}

	public String getName() {
		return name;
	}

	public boolean readFromClasspath() {
		try (InputStream in = SchematicStructure.class.getResourceAsStream(resourcePath)) {
			if (in == null) {
				CodeArenaMod.LOGGER.warn("Structure not on classpath: {}", resourcePath);
				return false;
			}
			CompoundTag nbt;
			try (DataInputStream data = new DataInputStream(new GZIPInputStream(in))) {
				nbt = NbtIo.read(data, NbtAccounter.unlimitedHeap());
			}
			this.length = nbt.getShortOr("Width", (short) 1);
			this.width = nbt.getShortOr("Length", (short) 1);
			this.height = nbt.getShortOr("Height", (short) 1);
			this.blocks = new Block[height][width][length];

			byte[] blockIds = nbt.getByteArray("Blocks").orElse(new byte[0]);
			int x = 1;
			int y = 1;
			int z = 1;
			for (int i = 0; i < blockIds.length; i++) {
				int blockId = blockIds[i] & 0xFF;
				blocks[y - 1][z - 1][x - 1] = LegacyBlockIds.fromId(blockId);
				x++;
				if (x > length) {
					x = 1;
					z++;
				}
				if (z > width) {
					z = 1;
					y++;
				}
			}
			loaded = true;
			CodeArenaMod.LOGGER.info("Loaded schematic {} ({}x{}x{}, {} block bytes)",
					resourcePath, length, height, width, blockIds.length);
			return true;
		} catch (Exception e) {
			CodeArenaMod.LOGGER.error("Failed to read schematic {}", resourcePath, e);
			loaded = false;
			return false;
		}
	}

	/**
	 * Places the structure centered on {@code center} (same offset convention as legacy Forge loader).
	 * @return nonzero blocks placed, or -1 if not loaded
	 */
	public int placeCentered(ServerLevel level, BlockPos center) {
		if (!loaded || blocks == null) {
			return -1;
		}
		int posX = center.getX() - length / 2 + 1;
		int posY = center.getY();
		int posZ = center.getZ() - width / 2 + 1;
		return placeAt(level, posX, posY, posZ);
	}

	/**
	 * Places with the structure's min-corner at {@code origin}.
	 */
	public int placeAt(ServerLevel level, BlockPos origin) {
		return placeAt(level, origin.getX(), origin.getY(), origin.getZ());
	}

	public int placeAt(ServerLevel level, int posX, int posY, int posZ) {
		if (!loaded || blocks == null) {
			return -1;
		}
		int placed = 0;
		for (int y = 0; y < height; y++) {
			for (int z = 0; z < width; z++) {
				for (int x = 0; x < length; x++) {
					Block block = blocks[y][z][x];
					if (block == null || block == Blocks.AIR) {
						continue;
					}
					BlockPos pos = new BlockPos(posX + x, posY + y, posZ + z);
					BlockState state = block.defaultBlockState();
					level.setBlock(pos, state, Block.UPDATE_CLIENTS);
					placed++;
				}
			}
		}
		CodeArenaMod.LOGGER.info("Placed {} blocks from {} at {},{},{}", placed, resourcePath, posX, posY, posZ);
		return placed;
	}

	public int getLength() {
		return length;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public boolean isLoaded() {
		return loaded;
	}
}
