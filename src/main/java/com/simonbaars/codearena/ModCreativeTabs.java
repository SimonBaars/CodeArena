package com.simonbaars.codearena;

import com.simonbaars.codearena.item.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {
	public static final ResourceKey<CreativeModeTab> CODEARENA_TAB =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, CodeArenaMod.id("codearena_tab"));

	private ModCreativeTabs() {}

	public static void register() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CODEARENA_TAB,
				CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
						.title(Component.translatable("itemGroup.codearena.codearena_tab"))
						.icon(() -> new ItemStack(ModItems.CHECKMARK))
						.displayItems((params, output) -> {
							output.accept(ModItems.CHECKMARK);
							output.accept(ModItems.CROSSMARK);
						})
						.build());
	}
}
