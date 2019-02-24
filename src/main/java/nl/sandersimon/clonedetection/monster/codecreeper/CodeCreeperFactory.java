package nl.sandersimon.clonedetection.monster.codecreeper;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class CodeCreeperFactory implements IRenderFactory<EntityCodeCreeper> {

	@Override
	public Render<? super EntityCodeCreeper> createRenderFor(RenderManager manager) {
		return new RenderCodeCreeper(manager);
	}

}

