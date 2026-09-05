package com.simonbaars.codearena.item;

import com.simonbaars.codearena.CodeArenaMod;
import com.simonbaars.codearena.challenge.ArenaSession;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/** Creative/utility item: spawn the procedural CodeArena at the player. */
public class ArenaSpawnItem extends Item {
	public ArenaSpawnItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.FAIL;
		}
		if (CodeArenaMod.activeSession != null && CodeArenaMod.activeSession.isActive()) {
			serverPlayer.sendSystemMessage(Component.translatable("commands.codearena.already"));
			return InteractionResult.FAIL;
		}
		CodeArenaMod.activeSession = ArenaSession.spawn(serverLevel, serverPlayer);
		serverPlayer.sendSystemMessage(Component.translatable("commands.codearena.spawn.success"));
		return InteractionResult.SUCCESS;
	}
}
