package com.simonbaars.codearena.minecraft.structureloader;

import com.simonbaars.codearena.minecraft.ICreatorBlock;

import net.minecraft.util.math.BlockPos;

public class OutlineCreator implements ICreatorBlock {
	SchematicStructure struct;
	BlockPos pos;
	int modifierx,modifiery,modifierz;
	public OutlineCreator(String name, BlockPos pos, int modifierx, int modifiery, int modifierz) {
		this.struct = new SchematicStructure(name+".structure");
    	struct.readFromFile();
    	this.pos=pos;
	}

	@Override
	public boolean run() {
		struct.showOutline(pos.getX(),modifierx, pos.getY(),modifiery, pos.getZ(),modifierz, this);
		return true;
	}

}
