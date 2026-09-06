package com.simonbaars.codearena.client;

import com.simonbaars.codearena.CodeArenaMod;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;

/**
 * Dev-only: {@code -Dcodearena.arenashot=1} + quickPlay arenaplay → {@code /codearena scan}
 * (disk sample-project AST wave) + overview + open-air spider texture shots, quit.
 */
public final class ArenaPlaytestShot {
	private static int ticks = -1;
	private static boolean spawned;
	private static boolean positioned;
	private static boolean shotArena;
	private static boolean shotSpider;
	private static boolean positionedSpider;
	private static int arenaShotTries;
	private static int spiderShotTries;

	private ArenaPlaytestShot() {}

	public static void registerIfRequested() {
		if (!"1".equals(System.getProperty("codearena.arenashot"))) {
			return;
		}
		CodeArenaMod.LOGGER.info("ArenaPlaytestShot armed");
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.level == null || client.player == null) {
				return;
			}
			if (client.gui.screen() != null || client.isPaused()) {
				client.setScreenAndShow(null);
			}
			if (ticks < 0) {
				ticks = 0;
			}
			ticks++;
			if (!spawned && ticks == 60) {
				var conn = client.player.connection;
				conn.sendCommand("gamemode creative @p");
				conn.sendCommand("time set noon");
				conn.sendCommand("weather clear");
				conn.sendCommand("codearena end");
			}
			if (!spawned && ticks == 100) {
				client.player.connection.sendCommand("codearena scan");
				spawned = true;
				CodeArenaMod.LOGGER.info("ArenaPlaytestShot: /codearena scan issued");
			}
			if (spawned && !positioned && ticks == 160) {
				var conn = client.player.connection;
				conn.sendCommand("tp @p ~ ~22 ~-32 0 55");
				conn.sendCommand("effect give @p minecraft:night_vision 180 0 true");
				positioned = true;
			}
			if (positioned && !shotArena && ticks >= 240) {
				if (tryShot(client, "04-arena-overview.png")) {
					shotArena = true;
					CodeArenaMod.LOGGER.info("ArenaPlaytestShot: arena overview screenshot");
				} else if (++arenaShotTries > 40) {
					shotArena = true;
					CodeArenaMod.LOGGER.warn("ArenaPlaytestShot: arena shot gave up");
				}
			}
			if (shotArena && !positionedSpider && ticks >= 260) {
				var conn = client.player.connection;
				// Open mesa air above arena center — summon branded spider for texture close-up
				conn.sendCommand("tp @p 0 145 -70 0 25");
				conn.sendCommand("kill @e[type=codearena:code_spider,distance=..16]");
				conn.sendCommand("summon codearena:code_spider 0 142 -66 {CustomName:'\"Duplication: texture check\"',CustomNameVisible:1b,NoAI:1b,PersistenceRequired:1b}");
				conn.sendCommand("effect give @p minecraft:night_vision 120 0 true");
				positionedSpider = true;
				CodeArenaMod.LOGGER.info("ArenaPlaytestShot: summoned open-air code spider for texture shot");
			}
			if (positionedSpider && !shotSpider && ticks >= 310) {
				if (tryShot(client, "05-code-spider-texture.png")) {
					shotSpider = true;
					CodeArenaMod.LOGGER.info("ArenaPlaytestShot: spider texture screenshot");
				} else if (++spiderShotTries > 40) {
					shotSpider = true;
					CodeArenaMod.LOGGER.warn("ArenaPlaytestShot: spider shot gave up");
				}
			}
			if (shotSpider && ticks >= 340) {
				CodeArenaMod.LOGGER.info("ArenaPlaytestShot: quitting");
				client.stop();
			}
		});
	}

	private static boolean tryShot(Minecraft client, String name) {
		if (client.gui.screen() != null || client.isPaused()) {
			client.setScreenAndShow(null);
			return false;
		}
		Screenshot.grab(
				client.gameDirectory,
				name,
				client.gameRenderer.mainRenderTarget(),
				1,
				msg -> CodeArenaMod.LOGGER.info("ArenaPlaytestShot saved {}: {}", name, msg.getString()));
		return true;
	}
}
