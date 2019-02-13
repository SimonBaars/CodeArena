package nl.sandersimon.clonedetection.minecraft.gui;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GUISetupCloneFinding {
	public static int GUIID = 1;
	public static HashMap guiinventory = new HashMap();
	public static IInventory CloneType;
	public static int cloneType = 1;

	public static class GuiContainerMod extends Container {

		World world;
		EntityPlayer entity;
		int x, y, z;

		public GuiContainerMod(World world, int x, int y, int z, EntityPlayer player) {
			this.world = world;
			this.entity = player;
			this.x = x;
			this.y = y;
			this.z = z;
			CloneType = new InventoryBasic("CloneType", true, 3);
			guiinventory.put("CloneType", CloneType);
			this.addSlotToContainer(new Slot(CloneType, 0, 23, 35) {

				public void onSlotChanged() {
					super.onSlotChanged();
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = server.getWorld(entity.dimension);
					if (getHasStack()) {
						cloneType = 1;
					}
				}
				
				public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
			    {
					if(stack.getItem()) {
						
					}
			        this.onSlotChanged();
			        return stack;
			    }
			});
			this.addSlotToContainer(new Slot(CloneType, 1, 101, 34) {

				public void onSlotChanged() {
					super.onSlotChanged();
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = server.getWorld(entity.dimension);
					if (getHasStack()) {
						cloneType = 2;
					}
				}
			});
			this.addSlotToContainer(new Slot(CloneType, 2, 183, 35) {

				public void onSlotChanged() {
					super.onSlotChanged();
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					World world = server.getWorld(entity.dimension);
					if (getHasStack()) {
						cloneType = 3;
					}
				}
			});
			int si;
			int sj;
			for (si = 0; si < 3; ++si)
				for (sj = 0; sj < 9; ++sj)
					this.addSlotToContainer(new Slot(player.inventory, sj + (si + 1) * 9, 37 + 8 + sj * 18, 17 + 84 + si * 18));
			for (si = 0; si < 9; ++si)
				this.addSlotToContainer(new Slot(player.inventory, si, 37 + 8 + si * 18, 17 + 142));
		}

		@Override
		public boolean canInteractWith(EntityPlayer player) {
			return true;
		}

		@Override
		public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
			ItemStack itemstack = null;
			Slot slot = (Slot) this.inventorySlots.get(index);
			if (slot != null && slot.getHasStack()) {
				ItemStack itemstack1 = slot.getStack();
				itemstack = itemstack1.copy();
				if (index < 9) {
					if (!this.mergeItemStack(itemstack1, 9, (45 - 9), true))
						return ItemStack.EMPTY;
				} else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
					return ItemStack.EMPTY;
				}
				if (itemstack1.getCount() == 0) {
					slot.putStack(ItemStack.EMPTY);
				} else {
					slot.onSlotChanged();
				}
				if (itemstack1.getCount() == itemstack.getCount()) {
					return ItemStack.EMPTY;
				}
				slot.onTake(playerIn, itemstack1);
			}
			return itemstack;
		}

		public void onContainerClosed(EntityPlayer playerIn) {
			super.onContainerClosed(playerIn);
		}
	}

	public static class GuiWindow extends GuiContainer {

		int x, y, z;
		EntityPlayer entity;
		GuiTextField InputProject;
		GuiTextField MinLines;

		public GuiWindow(World world, int x, int y, int z, EntityPlayer entity) {
			super(new GuiContainerMod(world, x, y, z, entity));
			this.x = x;
			this.y = y;
			this.z = z;
			this.entity = entity;
			this.xSize = 250;
			this.ySize = 200;
		}

		private static final ResourceLocation texture = new ResourceLocation("setupclonefinding.png");

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			this.drawDefaultBackground();
			super.drawScreen(mouseX, mouseY, partialTicks);
			this.renderHoveredToolTip(mouseX, mouseY);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawDefaultBackground();
			this.mc.renderEngine.bindTexture(texture);
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
			zLevel = 100.0F;
		}

		@Override
		protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			try {
				super.mouseClicked(mouseX, mouseY, mouseButton);
				InputProject.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
				MinLines.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			} catch (Exception ignored) {
			}
		}

		@Override
		public void updateScreen() {
			super.updateScreen();
			InputProject.updateCursorCounter();
			MinLines.updateCursorCounter();
		}

		@Override
		protected void keyTyped(char typedChar, int keyCode) {
			try {
				super.keyTyped(typedChar, keyCode);
				InputProject.textboxKeyTyped(typedChar, keyCode);
				MinLines.textboxKeyTyped(typedChar, keyCode);
			} catch (Exception ignored) {
			}
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int par1, int par2) {
			this.fontRenderer.drawString("Type 1", 41, 34, -1);
			this.fontRenderer.drawString("Type 2", 119, 33, -1);
			this.fontRenderer.drawString("Type 3", 200, 34, -1);
			this.fontRenderer.drawString("Please choose the clone type:", 21, 15, -1);
			InputProject.drawTextBox();
			this.fontRenderer.drawString("Please enter the Java project:", 22, 67, -1);
			MinLines.drawTextBox();
			this.fontRenderer.drawString("Please enter the min. amount of lines:", 22, 119, -1);
		}

		@Override
		public void onGuiClosed() {
			super.onGuiClosed();
			Keyboard.enableRepeatEvents(false);
		}

		@Override
		public void initGui() {
			super.initGui();
			this.guiLeft = (this.width - 250) / 2;
			this.guiTop = (this.height - 200) / 2;
			Keyboard.enableRepeatEvents(true);
			this.buttonList.clear();
			this.buttonList.add(new GuiButton(0, this.guiLeft + 127, this.guiTop + 170, 118, 20, "Start Battle!"));
			InputProject = new GuiTextField(0, this.fontRenderer, 21, 83, 120, 20);
			guiinventory.put("text:InputProject", InputProject);
			InputProject.setMaxStringLength(32767);
			InputProject.setFocused(true);
			InputProject.setText("");
			MinLines = new GuiTextField(1, this.fontRenderer, 23, 136, 120, 20);
			guiinventory.put("text:MinLines", MinLines);
			MinLines.setMaxStringLength(32767);
			MinLines.setFocused(true);
			MinLines.setText("6");
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			World world = server.getWorld(entity.dimension);
			if (button.id == 0) {
				{
					java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
					mcreator_generateArena.executeProcedure($_dependencies);
				}
			}
		}

		@Override
		public boolean doesGuiPauseGame() {
			return false;
		}
	}
}