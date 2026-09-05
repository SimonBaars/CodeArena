package com.simonbaars.codearena.item;

import com.simonbaars.codearena.CodeArenaMod;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class ModItems {
	public static Item CHECKMARK;
	public static Item CROSSMARK;

	private ModItems() {}

	public static void register() {
		CHECKMARK = register("checkmark", ArenaSpawnItem::new, new Item.Properties().stacksTo(1));
		CROSSMARK = register("crossmark", ArenaEndItem::new, new Item.Properties().stacksTo(1));
		CodeArenaMod.LOGGER.info("Registered CodeArena items");
	}

	private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties settings) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, CodeArenaMod.id(name));
		Item item = factory.apply(settings.setId(key));
		Registry.register(BuiltInRegistries.ITEM, key, item);
		return item;
	}
}
