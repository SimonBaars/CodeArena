package com.simonbaars.codearena;

import com.simonbaars.codearena.challenge.ArenaSession;
import com.simonbaars.codearena.command.CodeArenaCommands;
import com.simonbaars.codearena.item.ModItems;
import com.simonbaars.codearena.monster.ModEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fabric 26.2 entry — deepened subset of Forge 1.12.2 CodeArena / CloneDetection.
 * Schematic arena + watchtowers, expanded demo smells, custom entity registry ids,
 * package-filter diamonds. Swing editor N/A/deferred (Forge desktop UI); real AST needs CloneRefactor jar (demo-only without one).
 */
public class CodeArenaMod implements ModInitializer {
	public static final String MOD_ID = "codearena";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Active in-world arena session (single-player / one-at-a-time). */
	public static ArenaSession activeSession;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing CodeArena (Fabric 26.2 deepened subset)");
		ModEntities.register();
		ModItems.register();
		ModCreativeTabs.register();
		CodeArenaCommands.register();
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (activeSession != null && activeSession.isActive()) {
				activeSession.onEntityDeath(entity);
			}
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (activeSession == null || !activeSession.isActive()) {
				return;
			}
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				activeSession.tickFilter(player);
			}
		});
		LOGGER.info("CodeArena registered — /codearena spawn|end|problems|place");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
