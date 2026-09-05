package com.simonbaars.codearena;

import com.simonbaars.codearena.challenge.ArenaSession;
import com.simonbaars.codearena.command.CodeArenaCommands;
import com.simonbaars.codearena.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fabric 26.2 entry — deepened subset of Forge 1.12.2 CodeArena / CloneDetection.
 * Schematic arena load + demo problem→mob scoring are ported; Swing editor and AST engine are not.
 */
public class CodeArenaMod implements ModInitializer {
	public static final String MOD_ID = "codearena";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Active in-world arena session (single-player / one-at-a-time). */
	public static ArenaSession activeSession;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing CodeArena (Fabric 26.2 subset of Forge 1.12.2 CloneDetection)");
		ModItems.register();
		ModCreativeTabs.register();
		CodeArenaCommands.register();
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (activeSession != null && activeSession.isActive()) {
				activeSession.onEntityDeath(entity);
			}
		});
		LOGGER.info("CodeArena registered — /codearena spawn|end|problems");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
