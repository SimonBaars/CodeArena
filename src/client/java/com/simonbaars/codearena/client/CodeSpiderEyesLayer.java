package com.simonbaars.codearena.client;

import com.simonbaars.codearena.CodeArenaMod;
import net.minecraft.client.model.monster.spider.SpiderModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/**
 * Legacy {@code mobs/spider_eyes.png} → {@code textures/entity/code_spider_eyes.png}.
 * (Pixel-identical to vanilla eyes; wired for asset completeness.)
 */
public class CodeSpiderEyesLayer<M extends SpiderModel> extends EyesLayer<LivingEntityRenderState, M> {
	private static final RenderType EYES = RenderTypes.eyes(
			CodeArenaMod.id("textures/entity/code_spider_eyes.png"));

	public CodeSpiderEyesLayer(RenderLayerParent<LivingEntityRenderState, M> parent) {
		super(parent);
	}

	@Override
	public RenderType renderType() {
		return EYES;
	}
}
