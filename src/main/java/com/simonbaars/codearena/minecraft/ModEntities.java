package com.simonbaars.codearena.minecraft;

import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.monster.codecreeper.CodeCreeperFactory;
import com.simonbaars.codearena.monster.codecreeper.EntityCodeCreeper;
import com.simonbaars.codearena.monster.codeskeleton.CodeSkeletonFactory;
import com.simonbaars.codearena.monster.codeskeleton.EntityCodeSkeleton;
import com.simonbaars.codearena.monster.codespider.CodeSpiderFactory;
import com.simonbaars.codearena.monster.codespider.EntityCodeSpider;
import com.simonbaars.codearena.monster.codezombie.CodeZombieFactory;
import com.simonbaars.codearena.monster.codezombie.EntityCodeZombie;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModEntities {

    public static void init() {
        // Every entity in our mod has an ID (local to this mod)
        int id = 1;
        EntityRegistry.registerModEntity(new ResourceLocation(CloneDetection.MODID, "codespider"), EntityCodeSpider.class, "CodeSpider", id++, CloneDetection.get(), 64, 3, true, 0x996600, 0x00ff00);
        EntityRegistry.registerModEntity(new ResourceLocation(CloneDetection.MODID, "codecreeper"), EntityCodeCreeper.class, "CodeCreeper", id++, CloneDetection.get(), 64, 3, true, 0x996600, 0x00ff00);
        EntityRegistry.registerModEntity(new ResourceLocation(CloneDetection.MODID, "codeskeleton"), EntityCodeSkeleton.class, "CodeSkeleton", id++, CloneDetection.get(), 64, 3, true, 0x996600, 0x00ff00);
        EntityRegistry.registerModEntity(new ResourceLocation(CloneDetection.MODID, "codezombie"), EntityCodeZombie.class, "CodeZombie", id++, CloneDetection.get(), 64, 3, true, 0x996600, 0x00ff00);

        // We want our mob to spawn in Plains and ice plains biomes. If you don't add this then it will not spawn automatically
        // but you can of course still make it spawn manually
        //EntityRegistry.addSpawn(EntityWeirdZombie.class, 100, 3, 5, EnumCreatureType.MONSTER, Biomes.PLAINS, Biomes.ICE_PLAINS);

        // This is the loot table for our mob
        //LootTableList.register(EntityWeirdZombie.LOOT);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        RenderingRegistry.registerEntityRenderingHandler(EntityCodeSpider.class, new CodeSpiderFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityCodeSkeleton.class, new CodeSkeletonFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityCodeCreeper.class, new CodeCreeperFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityCodeZombie.class, new CodeZombieFactory());
    }
}
