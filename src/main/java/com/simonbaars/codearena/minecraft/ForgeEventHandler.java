package com.simonbaars.codearena.minecraft;

import java.io.File;

import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.common.SavePaths;
import com.simonbaars.codearena.thread.ProblemDetectionThread;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ForgeEventHandler {
	public static int searchingPage = 1;
	boolean searchByTitle;
	
	@SubscribeEvent
	public void livingSpawnEvent(WorldEvent.Load event){
		CloneDetection.dialoge=0;
	}
		
	@SubscribeEvent
	public void playerChat(ServerChatEvent event){
		if(CloneDetection.dialoge!=0){
			if(CloneDetection.dialoge==1){
				try{
					int inputNumber = Integer.parseInt(event.getMessage());
					String[] projects = new File(SavePaths.getProjectFolder()).list();
					if(inputNumber < 1 || inputNumber > projects.length){
						CloneDetection.get().eventHandler.delayedPrints.add("This is not a valid number of a project. Please enter a number between 1-"+projects.length+".");
					} else {
						//CloneDetection.eventHandler.delayedPrints.add("Thank you. We'll generate a beatiful city out of the clones of this project.");
						CloneDetection.dialoge=0;
						ProblemDetectionThread.startWorker(event.getPlayer(), projects[inputNumber-1]);
					}
				} catch (Exception e){
					CloneDetection.get().eventHandler.delayedPrints.add("Please only use numbers");
				}
			}
		}
	}
}
