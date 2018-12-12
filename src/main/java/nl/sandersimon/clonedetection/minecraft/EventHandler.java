package nl.sandersimon.clonedetection.minecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.minecraft.structureloader.LightUpdateCheck;
import nl.sandersimon.clonedetection.minecraft.structureloader.SchematicStructure;

public class EventHandler {
	public final ArrayList<SchematicStructure> postProcessors = new ArrayList<SchematicStructure>();
	public final ArrayList<ICreatorBlock> creators = new ArrayList<ICreatorBlock>();
	public final ArrayList<ICreatorBlock> serverCreators = new ArrayList<ICreatorBlock>();
	public final ArrayList<String> delayedPrints = new ArrayList<String>();
	public LightUpdateCheck lightUpdate;
	public long previousTick = 0;
	public boolean isLoaded=false;
	
	public EventHandler(){
	}
	
		
	public void load(){		
			loadStructures();
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
	public void update(TickEvent.ServerTickEvent event){
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
		
		//lastUpdateTime=System.currentTimeMillis();
			//System.out.println("IMSM took "+(System.currentTimeMillis()-tickTime));
	}
	
	private void loadStructures() {
		creators.clear();
		int i = 0;
		while(fileExists2(i+".txt")){
			//System.out.println("Loop 5");
			String[] array = new String[9];
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(SavePaths.getSaveFolder()+Minecraft.getMinecraft().getIntegratedServer().getFolderName()+"/Structures/"+i+".txt"));
			

			for(int j = 0; j<array.length; j++){
				//System.out.println("Loop 6");
				try {
					array[j]=in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				in.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			creators.add(new StructureCreatorClient(array[0], Integer.parseInt(array[2]),Integer.parseInt(array[3]),Integer.parseInt(array[4]), array[1].equals("true"),i));
			((StructureCreator)creators.get(creators.size()-1)).i=Integer.parseInt(array[5]);
			((StructureCreator)creators.get(creators.size()-1)).j=Integer.parseInt(array[6]);
			((StructureCreator)creators.get(creators.size()-1)).k=Integer.parseInt(array[7]);
			((StructureCreator)creators.get(creators.size()-1)).speedUp=Integer.parseInt(array[8]);
			}catch (FileNotFoundException e1) {
				e1.printStackTrace();
			
			}
			i++;
		}
		
	}
	
	boolean fileExists2(String path){
		File f = new File(SavePaths.getSaveFolder()+Minecraft.getMinecraft().getIntegratedServer().getFolderName()+"/Structures/"+path);
		if(f.exists() && !f.isDirectory()) { 
		    return true;
		}
		return false;
	}
}
