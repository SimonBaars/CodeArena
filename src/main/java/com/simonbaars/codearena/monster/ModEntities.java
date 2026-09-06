package com.simonbaars.codearena.monster;

import com.simonbaars.codearena.CodeArenaMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Zombie;

/**
 * Distinct registry ids for smell mobs (vanilla classes + custom textures where assets exist).
 * Legacy audit: only {@code ModelCodeSkeleton} existed (vanilla thin-biped clone, not Techne);
 * only {@code mobs/spider.png} (+ eyes) were custom textures — ported as {@code code_spider*} /
 * {@code code_cave_spider} (darkened derivative for type-2).
 */
public final class ModEntities {
	public static EntityType<Spider> CODE_SPIDER;
	public static EntityType<Zombie> CODE_ZOMBIE;
	public static EntityType<Skeleton> CODE_SKELETON;
	public static EntityType<Creeper> CODE_CREEPER;
	public static EntityType<CaveSpider> CODE_CAVE_SPIDER;
	public static EntityType<Witch> CODE_WITCH;
	public static EntityType<Blaze> CODE_BLAZE;
	public static EntityType<EnderMan> CODE_ENDERMAN;

	private ModEntities() {}

	public static void register() {
		CODE_SPIDER = register("code_spider",
				EntityType.Builder.of(Spider::new, MobCategory.MONSTER).sized(1.4f, 0.9f).clientTrackingRange(8));
		CODE_ZOMBIE = register("code_zombie",
				EntityType.Builder.<Zombie>of((type, level) -> new Zombie(type, level), MobCategory.MONSTER)
						.sized(0.6f, 1.95f).clientTrackingRange(8));
		CODE_SKELETON = register("code_skeleton",
				EntityType.Builder.of(Skeleton::new, MobCategory.MONSTER).sized(0.6f, 1.99f).clientTrackingRange(8));
		CODE_CREEPER = register("code_creeper",
				EntityType.Builder.of(Creeper::new, MobCategory.MONSTER).sized(0.6f, 1.7f).clientTrackingRange(8));
		CODE_CAVE_SPIDER = register("code_cave_spider",
				EntityType.Builder.of(CaveSpider::new, MobCategory.MONSTER).sized(0.7f, 0.5f).clientTrackingRange(8));
		CODE_WITCH = register("code_witch",
				EntityType.Builder.of(Witch::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
		CODE_BLAZE = register("code_blaze",
				EntityType.Builder.of(Blaze::new, MobCategory.MONSTER).sized(0.6f, 1.8f).clientTrackingRange(8));
		CODE_ENDERMAN = register("code_enderman",
				EntityType.Builder.of(EnderMan::new, MobCategory.MONSTER).sized(0.6f, 2.9f).clientTrackingRange(8));

		FabricDefaultAttributeRegistry.register(CODE_SPIDER, Spider.createAttributes());
		FabricDefaultAttributeRegistry.register(CODE_ZOMBIE, Zombie.createAttributes());
		FabricDefaultAttributeRegistry.register(CODE_SKELETON, AbstractSkeleton.createAttributes());
		FabricDefaultAttributeRegistry.register(CODE_CREEPER, Creeper.createAttributes());
		FabricDefaultAttributeRegistry.register(CODE_CAVE_SPIDER, CaveSpider.createCaveSpider());
		FabricDefaultAttributeRegistry.register(CODE_WITCH, Witch.createAttributes());
		FabricDefaultAttributeRegistry.register(CODE_BLAZE, Blaze.createAttributes());
		FabricDefaultAttributeRegistry.register(CODE_ENDERMAN, EnderMan.createAttributes());

		CodeArenaMod.LOGGER.info("Registered CodeArena smell entity types (8)");
	}

	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, CodeArenaMod.id(name));
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
	}
}
