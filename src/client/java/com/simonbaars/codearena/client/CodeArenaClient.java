package com.simonbaars.codearena.client;

import com.simonbaars.codearena.monster.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client entry. Smell renderers: custom spider + cave-spider skins; vanilla for zombie/skeleton/creeper/etc.
 * {@code ModelCodeSkeleton} in legacy is vanilla thin-biped (not Techne) — {@link SkeletonRenderer} already covers it.
 * Legacy Swing {@code CodeEditor} / RSyntaxTextArea is N/A/deferred (Forge desktop UI, not portable to Fabric client).
 */
public class CodeArenaClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("codearena");

	@Override
	public void onInitializeClient() {
		ArenaPlaytestShot.registerIfRequested();
		EntityRenderers.register(ModEntities.CODE_SPIDER, CodeSpiderRenderer::new);
		EntityRenderers.register(ModEntities.CODE_CAVE_SPIDER, CodeCaveSpiderRenderer::new);
		EntityRenderers.register(ModEntities.CODE_ZOMBIE, ZombieRenderer::new);
		EntityRenderers.register(ModEntities.CODE_SKELETON, SkeletonRenderer::new);
		EntityRenderers.register(ModEntities.CODE_CREEPER, CreeperRenderer::new);
		EntityRenderers.register(ModEntities.CODE_WITCH, WitchRenderer::new);
		EntityRenderers.register(ModEntities.CODE_BLAZE, BlazeRenderer::new);
		EntityRenderers.register(ModEntities.CODE_ENDERMAN, EndermanRenderer::new);
		LOGGER.info("CodeArena client init — custom spider/cave-spider textures; other smells vanilla; no Techne ModelCode*");
	}
}
