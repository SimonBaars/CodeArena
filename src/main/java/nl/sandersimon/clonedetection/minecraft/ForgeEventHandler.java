package nl.sandersimon.clonedetection.minecraft;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nl.sandersimon.clonedetection.CloneDetection;

public class ForgeEventHandler {
	public static int searchingPage = 1;
	boolean searchByTitle;
	
	@SubscribeEvent
	public void livingSpawnEvent(WorldEvent.Load event)
	{
		CloneDetection.dialoge=0;
	}
		
	@SubscribeEvent
	public void playerChat(ServerChatEvent event){
		if(CloneDetection.dialoge!=0){
			if(CloneDetection.dialoge==1){
				try{
					int inputNumber = Integer.parseInt(event.getMessage());
					if(inputNumber<61){
						CloneDetection.eventHandler.delayedPrints.add("You cannot fly a negative (or too low) distance. Please insert a positive number above 60.");
					} else {
						CloneDetection.eventHandler.delayedPrints.add("Thank you. Please hop aboard quickly, we are about to depart in 10 seconds.");
						CloneDetection.dialoge=0;
					}
				} catch (Exception e){
					//IMSM.eventHandler.delayedPrints.add("Please only use numbers");
				}
			}
		}
	}
}
