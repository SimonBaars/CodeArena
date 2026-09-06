package com.simonbaars.codearena.client;

import com.simonbaars.codearena.CodeArenaMod;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Screenshot;

/**
 * Dev-only: {@code -Dcodearena.arenashot=1} + quickPlay arenaplay → spawn arena, screenshot, quit.
 */
public final class ArenaPlaytestShot {
	private static int ticks = -1;
	private static boolean spawned;
	private static boolean positioned;
	private static boolean shotArena;

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
			if (client.isPaused()) {
				client.setScreenAndShow(null);
			}
			if (ticks < 0) {
				ticks = 0;
			}
			ticks++;
			if (!spawned && ticks == 40) {
				var conn = client.player.connection;
				conn.sendCommand("gamemode creative @p");
				conn.sendCommand("time set noon");
				conn.sendCommand("weather clear");
				conn.sendCommand("codearena end");
			}
			if (!spawned && ticks == 70) {
				var conn = client.player.connection;
				conn.sendCommand("codearena spawn");
				spawned = true;
				CodeArenaMod.LOGGER.info("ArenaPlaytestShot: /codearena spawn issued");
			}
			if (spawned && !positioned && ticks == 120) {
				var conn = client.player.connection;
				// Relative overview: up and south of arena center (spawn already TPs player to center)
				conn.sendCommand("tp @p ~ ~18 ~-28 0 50");
				conn.sendCommand("effect give @p minecraft:night_vision 120 0 true");
				positioned = true;
			}
			if (positioned && !shotArena && ticks == 200) {
				Screenshot.grab(client, false);
				shotArena = true;
				CodeArenaMod.LOGGER.info("ArenaPlaytestShot: screenshot requested");
			}
			if (shotArena && ticks == 230) {
				CodeArenaMod.LOGGER.info("ArenaPlaytestShot: quitting");
				client.stop();
			}
		});
	}
}
