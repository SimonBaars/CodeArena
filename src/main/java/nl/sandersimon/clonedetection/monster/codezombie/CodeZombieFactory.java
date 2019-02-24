package nl.sandersimon.clonedetection.monster.codezombie;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class CodeZombieFactory implements IRenderFactory<EntityCodeZombie> {

	@Override
	public Render<? super EntityCodeZombie> createRenderFor(RenderManager manager) {
		return new RenderCodeZombie(manager);
	}

}

