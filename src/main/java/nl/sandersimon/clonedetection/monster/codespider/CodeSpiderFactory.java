package nl.sandersimon.clonedetection.monster.codespider;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class CodeSpiderFactory implements IRenderFactory<EntityCodeSpider> {

	@Override
	public Render<? super EntityCodeSpider> createRenderFor(RenderManager manager) {
		return new RenderCodeSpider<>(manager);
	}

}

