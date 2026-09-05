package com.simonbaars.codearena.minecraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.simonbaars.codearena.challenge.Challenges;
import com.simonbaars.codearena.common.SavePaths;
import com.simonbaars.codearena.minecraft.structureloader.LightUpdateCheck;
import com.simonbaars.codearena.minecraft.structureloader.SchematicStructure;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CDEventHandler {
	public final List<SchematicStructure> postProcessors = new ArrayList<>();
	public final List<ICreatorBlock> creators = new ArrayList<>();
	public final List<ICreatorBlock> serverCreators = new ArrayList<>();
	public final List<String> delayedPrints = new ArrayList<>();
	public final List<Runnable> nextTickActions = new ArrayList<>();
	public LightUpdateCheck lightUpdate;
	public long previousTick = 0;
	public boolean isLoaded=false;
	public Challenges challenge=null;
	
	public CDEventHandler(){
	}
	
		
	public void load(){		
			lightUpdate= new LightUpdateCheck(Minecraft.getMinecraft().world, Minecraft.getMinecraft().getIntegratedServer().getEntityWorld());
			isLoaded=true;
	}
	
	private void loadLanguageFile() {
		try {
			File file = new File("structures/en_US.lang");
			if(file.exists()){
			InputStream languageFile = new FileInputStream(file);
			LanguageMap.inject(languageFile);
			languageFile.close();
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	@SubscribeEvent
	public void update(TickEvent.ClientTickEvent event){
		if(!isLoaded && Minecraft.getMinecraft().world!=null)
			load();
		if(challenge!= null) challenge.run();
		/*for(int i = 0; i<liveCreators.size(); i++){
			if(System.currentTimeMillis()>liveCreators.get(i).lastTickTime+liveCreators.get(i).waitTime){
				if(liveCreators.get(i).run() && liveCreators.get(i).boundTo.run()){
					i--;
					continue;
				}
				if(i<liveCreators.size() && liveCreators.get(i)!=null){
				liveCreators.get(i).lastTickTime=System.currentTimeMillis();
				}
			}
		}*/
		try{
		//if(isRiding!=null){
						
			if(creators.size()>0){
				creators.clear();
			}
			
			/*for(int i = 0; i<creators.size(); i++){
				if((!(creators.get(i) instanceof StructureCreator) || 
						((StructureCreator)creators.get(i)).boundTo.generatedBlocks>((StructureCreator)creators.get(i)).generatedBlocks) && 
						creators.get(i).run()){
					creators.remove(i);
					i--;
				}
			}*/
		//}
	//}
			if(isLoaded){
				//lightUpdate.runClient();
				lightUpdate.clientProcesses.clear();
				}
		} catch (Exception e){
			e.printStackTrace();
		}
		}
	
	
	@SubscribeEvent
	public void update(TickEvent.WorldTickEvent event){
		if(event.side.isServer()) {
		while(nextTickActions.size()!=0) {
			nextTickActions.get(0).run();
			nextTickActions.remove(0);
		}
		for(int i = 0; i<serverCreators.size(); i++){
			if(serverCreators.get(i).run()){
				serverCreators.remove(i);
				i--;
			}
		}
				
		//System.out.println("render"+System.currentTimeMillis());
	//long tickTime = System.currentTimeMillis();
	
		if(postProcessors.size()>0){
			postProcessors.get(0).postProcess();
			//System.out.println("Post processing done");
			postProcessors.remove(0);
		}
			
			
			
			while(delayedPrints.size()>0 && Minecraft.getMinecraft().getIntegratedServer().isCallingFromMinecraftThread()){
					Minecraft.getMinecraft().player.sendChatMessage(delayedPrints.get(0));
					
				delayedPrints.remove(0);
			}
			if(isLoaded){
				lightUpdate.runServer();
	}
		}
		//lastUpdateTime=System.currentTimeMillis();
			//System.out.println("IMSM took "+(System.currentTimeMillis()-tickTime));
	}
	
		
	boolean fileExists2(String path){
		File f = new File(SavePaths.getSaveFolder()+Minecraft.getMinecraft().getIntegratedServer().getFolderName()+"/Structures/"+path);
		if(f.exists() && !f.isDirectory()) { 
		    return true;
		}
		return false;
	}
}
