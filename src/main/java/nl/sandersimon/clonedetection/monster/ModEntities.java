package nl.sandersimon.clonedetection.monster;

import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.monster.codespider.CodeSpiderFactory;
import nl.sandersimon.clonedetection.monster.codespider.EntityCodeSpider;

public class ModEntities {

    public static void init() {
        // Every entity in our mod has an ID (local to this mod)
        int id = 1;
        EntityRegistry.registerModEntity(LootTableList.ENTITIES_SPIDER, EntityCodeSpider.class, "CodeSpider", id++, CloneDetection.get(), 128, 3, true, 0x996600, 0x00ff00);

        // This is the loot table for our mob
        // LootTableList.register(LootTableList.ENTITIES_SPIDER);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        RenderingRegistry.registerEntityRenderingHandler(EntityCodeSpider.class, new CodeSpiderFactory());
    }
}