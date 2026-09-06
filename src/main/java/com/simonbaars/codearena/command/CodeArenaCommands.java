package com.simonbaars.codearena.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.simonbaars.codearena.CodeArenaMod;
import com.simonbaars.codearena.challenge.ArenaBuilder;
import com.simonbaars.codearena.challenge.ArenaSession;
import com.simonbaars.codearena.javaparser.SampleProject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class CodeArenaCommands {
	private CodeArenaCommands() {}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("codearena")
					.then(Commands.literal("spawn")
							.executes(ctx -> {
								ServerPlayer player = ctx.getSource().getPlayerOrException();
								ServerLevel level = player.level();
								if (CodeArenaMod.activeSession != null && CodeArenaMod.activeSession.isActive()) {
									ctx.getSource().sendFailure(Component.translatable("commands.codearena.already"));
									return 0;
								}
								CodeArenaMod.activeSession = ArenaSession.spawn(level, player);
								ctx.getSource().sendSuccess(() -> Component.translatable("commands.codearena.spawn.success"), false);
								return 1;
							}))
					.then(Commands.literal("scan")
							.executes(ctx -> runScan(ctx.getSource().getPlayerOrException(), null))
							.then(Commands.argument("path", StringArgumentType.greedyString())
									.executes(ctx -> runScan(
											ctx.getSource().getPlayerOrException(),
											StringArgumentType.getString(ctx, "path")))))
					.then(Commands.literal("end")
							.executes(ctx -> {
								if (CodeArenaMod.activeSession == null || !CodeArenaMod.activeSession.isActive()) {
									ctx.getSource().sendFailure(Component.translatable("commands.codearena.none"));
									return 0;
								}
								CodeArenaMod.activeSession.end();
								CodeArenaMod.activeSession = null;
								ctx.getSource().sendSuccess(() -> Component.translatable("commands.codearena.end.success"), false);
								return 1;
							}))
					.then(Commands.literal("problems")
							.executes(ctx -> {
								ServerPlayer player = ctx.getSource().getPlayerOrException();
								if (CodeArenaMod.activeSession == null || !CodeArenaMod.activeSession.isActive()) {
									ctx.getSource().sendFailure(Component.translatable("commands.codearena.none"));
									return 0;
								}
								CodeArenaMod.activeSession.listProblems(player);
								return 1;
							}))
					.then(Commands.literal("place")
							.then(Commands.argument("structure", StringArgumentType.word())
									.suggests((ctx, builder) -> {
										for (String s : new String[]{"arena", "watchtower", "arenacheck", "coliseum", "colloseum"}) {
											builder.suggest(s);
										}
										return builder.buildFuture();
									})
									.executes(ctx -> {
										ServerPlayer player = ctx.getSource().getPlayerOrException();
										ServerLevel level = player.level();
										String name = StringArgumentType.getString(ctx, "structure");
										if ("coliseum".equalsIgnoreCase(name) || "colloseum".equalsIgnoreCase(name)) {
											ctx.getSource().sendSuccess(() -> Component.literal(
													"Placing large schematic '" + name + "' — may hitch briefly..."), false);
										}
										int placed = ArenaBuilder.placeNamed(level, player.blockPosition(), name);
										if (placed < 0) {
											ctx.getSource().sendFailure(Component.literal(
													"Unknown or unloadable structure: " + name
															+ " (try arena|watchtower|arenacheck|coliseum|colloseum)"));
											return 0;
										}
										int finalPlaced = placed;
										ctx.getSource().sendSuccess(() -> Component.literal(
												"Placed " + finalPlaced + " blocks from " + name + ".structure"), false);
										return 1;
									}))));

			dispatcher.register(Commands.literal("codeclones")
					.executes(ctx -> {
						ctx.getSource().sendSuccess(() -> Component.translatable("commands.codeclones.stub"), false);
						return 1;
					}));

			dispatcher.register(Commands.literal("codearenaend")
					.redirect(dispatcher.getRoot().getChild("codearena").getChild("end")));
		});
	}

	private static int runScan(ServerPlayer player, String rawPath) {
		if (CodeArenaMod.activeSession != null && CodeArenaMod.activeSession.isActive()) {
			player.sendSystemMessage(Component.translatable("commands.codearena.already"));
			return 0;
		}
		MinecraftServer server = player.level().getServer();
		Path gameDir = server.getServerDirectory().toAbsolutePath().normalize();
		Path scanRoot;
		try {
			scanRoot = resolveScanPath(gameDir, rawPath);
		} catch (IOException e) {
			player.sendSystemMessage(Component.literal("Scan path error: " + e.getMessage()));
			return 0;
		} catch (IllegalArgumentException e) {
			player.sendSystemMessage(Component.literal(e.getMessage()));
			return 0;
		}
		if (!Files.isDirectory(scanRoot)) {
			player.sendSystemMessage(Component.literal(
					"Not a directory (scoped to game dir " + gameDir + "): " + scanRoot));
			return 0;
		}
		player.sendSystemMessage(Component.literal(
				"Scanning .java under " + scanRoot + " (server-side; max 200 files; method-level JavaParser — not CloneRefactor Type-2/3)..."));
		CodeArenaMod.activeSession = ArenaSession.spawnFromDirectory(player.level(), player, scanRoot);
		player.sendSystemMessage(Component.translatable("commands.codearena.scan.success"));
		return 1;
	}

	/**
	 * Resolve scan path under the Minecraft game/server directory.
	 * Null/blank → bundled {@code codearena-sample} (materialized from jar resources).
	 * Relative paths resolve against gameDir; absolute paths must stay under gameDir.
	 */
	static Path resolveScanPath(Path gameDir, String rawPath) throws IOException {
		Path normalizedGame = gameDir.toAbsolutePath().normalize();
		if (rawPath == null || rawPath.isBlank() || "sample".equalsIgnoreCase(rawPath.trim())) {
			return SampleProject.ensureOnDisk(normalizedGame);
		}
		Path requested = Path.of(rawPath.trim());
		Path resolved = requested.isAbsolute()
				? requested.toAbsolutePath().normalize()
				: normalizedGame.resolve(requested).toAbsolutePath().normalize();
		if (!resolved.startsWith(normalizedGame)) {
			throw new IllegalArgumentException(
					"Refusing path outside game directory (" + normalizedGame + "): " + rawPath);
		}
		return resolved;
	}
}
