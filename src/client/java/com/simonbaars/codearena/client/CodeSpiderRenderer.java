package com.simonbaars.codearena.client;

import com.simonbaars.codearena.CodeArenaMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.spider.Spider;

/**
 * Uses legacy {@code mobs/spider.png} shipped as {@code textures/entity/code_spider.png}.
 * Other smell types keep vanilla renderer skins (no separate model assets in the Forge tree).
 */
public class CodeSpiderRenderer extends SpiderRenderer<Spider> {
	private static final Identifier TEXTURE = CodeArenaMod.id("textures/entity/code_spider.png");

	public CodeSpiderRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return TEXTURE;
	}
}
