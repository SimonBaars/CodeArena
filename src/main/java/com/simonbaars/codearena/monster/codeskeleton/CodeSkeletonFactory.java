package com.simonbaars.codearena.monster.codeskeleton;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class CodeSkeletonFactory implements IRenderFactory<EntityCodeSkeleton> {

	@Override
	public Render<? super EntityCodeSkeleton> createRenderFor(RenderManager manager) {
		return new RenderCodeSkeleton(manager);
	}

}

