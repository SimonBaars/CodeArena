package com.simonbaars.codearena.client;

import com.simonbaars.codearena.CodeArenaMod;
import net.minecraft.client.renderer.entity.CaveSpiderRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/**
 * Type-2 clone stand-in: darkened derivative of legacy {@code mobs/spider.png}
 * (no separate cave texture existed in Forge tree).
 */
public class CodeCaveSpiderRenderer extends CaveSpiderRenderer {
	private static final Identifier TEXTURE = CodeArenaMod.id("textures/entity/code_cave_spider.png");

	public CodeCaveSpiderRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.layers.removeIf(layer -> layer instanceof SpiderEyesLayer);
		this.addLayer(new CodeSpiderEyesLayer<>(this));
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return TEXTURE;
	}
}
