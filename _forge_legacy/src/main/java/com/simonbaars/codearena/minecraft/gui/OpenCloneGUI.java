package com.simonbaars.codearena.minecraft.gui;

import com.simonbaars.codearena.CloneDetection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class OpenCloneGUI {

	public static void executeProcedure(World world, Entity entity, int x, int y, int z) {
		if (entity instanceof EntityPlayer) {
			if(CloneDetection.get().getArena() == null) 
				((EntityPlayer) entity).openGui(CloneDetection.get(), GUISetupCloneFinding.GUIID, world, x, y, z);
			else ((EntityPlayer) entity).openGui(CloneDetection.get(), EndChallengeGUI.GUIID, world, x, y, z);
		}
	}
}
