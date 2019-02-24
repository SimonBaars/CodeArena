package nl.sandersimon.clonedetection.minecraft;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.monster.codecreeper.CodeCreeperFactory;
import nl.sandersimon.clonedetection.monster.codecreeper.EntityCodeCreeper;
import nl.sandersimon.clonedetection.monster.codeskeleton.CodeSkeletonFactory;
import nl.sandersimon.clonedetection.monster.codeskeleton.EntityCodeSkeleton;
import nl.sandersimon.clonedetection.monster.codespider.CodeSpiderFactory;
import nl.sandersimon.clonedetection.monster.codespider.EntityCodeSpider;
import nl.sandersimon.clonedetection.monster.codezombie.CodeZombieFactory;
import nl.sandersimon.clonedetection.monster.codezombie.EntityCodeZombie;

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
