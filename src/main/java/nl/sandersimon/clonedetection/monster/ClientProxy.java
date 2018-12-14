package nl.sandersimon.clonedetection.monster;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
        @Override
        public void preInit(FMLPreInitializationEvent e) {

            ModEntities.initModels();
        }
}