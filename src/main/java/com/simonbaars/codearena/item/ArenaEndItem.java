package com.simonbaars.codearena.item;

import com.simonbaars.codearena.CodeArenaMod;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/** Creative/utility item: end the active CodeArena session. */
public class ArenaEndItem extends Item {
	public ArenaEndItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.FAIL;
		}
		if (CodeArenaMod.activeSession == null || !CodeArenaMod.activeSession.isActive()) {
			serverPlayer.sendSystemMessage(Component.translatable("commands.codearena.none"));
			return InteractionResult.FAIL;
		}
		CodeArenaMod.activeSession.end();
		CodeArenaMod.activeSession = null;
		serverPlayer.sendSystemMessage(Component.translatable("commands.codearena.end.success"));
		return InteractionResult.SUCCESS;
	}
}
