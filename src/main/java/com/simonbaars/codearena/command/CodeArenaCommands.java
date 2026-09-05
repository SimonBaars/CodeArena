package com.simonbaars.codearena.command;

import com.simonbaars.codearena.CodeArenaMod;
import com.simonbaars.codearena.challenge.ArenaSession;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
							})));

			dispatcher.register(Commands.literal("codeclones")
					.executes(ctx -> {
						ctx.getSource().sendSuccess(() -> Component.translatable("commands.codeclones.stub"), false);
						return 1;
					}));

			dispatcher.register(Commands.literal("codearenaend")
					.redirect(dispatcher.getRoot().getChild("codearena").getChild("end")));
		});
	}
}
