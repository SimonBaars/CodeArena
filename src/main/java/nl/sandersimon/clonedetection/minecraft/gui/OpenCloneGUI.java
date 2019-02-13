package nl.sandersimon.clonedetection.minecraft.gui;

import net.minecraft.world.World;
import nl.sandersimon.clonedetection.CloneDetection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.HashMap;

public class OpenCloneGUI {

	public static void executeProcedure(World world, Entity entity, int x, int y, int z) {
		if (entity instanceof EntityPlayer)
			((EntityPlayer) entity).openGui(CloneDetection.get(), GUISetupCloneFinding.GUIID, world, x, y, z);
	}
}
