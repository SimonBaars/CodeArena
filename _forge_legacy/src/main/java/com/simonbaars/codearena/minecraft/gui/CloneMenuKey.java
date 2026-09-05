package com.simonbaars.codearena.minecraft.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CloneMenuKey {

	@SideOnly(Side.CLIENT)
	public void registerRenderers() {
		MinecraftForge.EVENT_BUS.register(new KeyHandler());
	}

	public static class KeyHandler {

		private final KeyBinding keys;

		public KeyHandler() {
			keys = new KeyBinding("key.mcreator.clonemenu", Keyboard.KEY_C, "key.categories.misc");
			ClientRegistry.registerKeyBinding(keys);
		}

		@SubscribeEvent
		public void onKeyInput(InputEvent.KeyInputEvent event) {
			if (!FMLClientHandler.instance().isGUIOpen(GuiChat.class)) {
				if (org.lwjgl.input.Keyboard.isKeyDown(keys.getKeyCode())) {
					EntityPlayer entitySP = Minecraft.getMinecraft().player;
					int x = (int) entitySP.posX;
					int y = (int) entitySP.posY;
					int z = (int) entitySP.posZ;
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = server.getWorld(entitySP.dimension);
					EntityPlayer entity = entitySP;
					for (EntityPlayer entityMP : world.playerEntities)
						if (entityMP.getName().equals(entitySP.getName()))
							entity = entityMP;
					OpenCloneGUI.executeProcedure(world, entity, x, y, z);
				}
			}
		}
	}
}
