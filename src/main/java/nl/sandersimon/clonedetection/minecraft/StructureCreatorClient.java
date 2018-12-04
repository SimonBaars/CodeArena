package nl.sandersimon.clonedetection.minecraft;

import net.minecraft.client.Minecraft;

public class StructureCreatorClient extends StructureCreator {

	public StructureCreatorClient(String name, int i, int j, int k, boolean doReplaceAir, int id) {
		super(name, Minecraft.getMinecraft().world, i, j, k, doReplaceAir, id);
	}

}
