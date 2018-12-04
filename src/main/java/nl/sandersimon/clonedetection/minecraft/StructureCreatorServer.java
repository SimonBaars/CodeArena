package nl.sandersimon.clonedetection.minecraft;

import net.minecraft.client.Minecraft;

public class StructureCreatorServer extends StructureCreator {

	public StructureCreatorServer(String name, int i, int j, int k, boolean doReplaceAir, int id) {
		super(name, Minecraft.getMinecraft().getIntegratedServer().getEntityWorld(), i, j, k, doReplaceAir, id);
	}

}
