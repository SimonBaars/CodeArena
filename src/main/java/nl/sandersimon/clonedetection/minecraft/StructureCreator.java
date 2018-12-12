package nl.sandersimon.clonedetection.minecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.minecraft.structureloader.SchematicStructure;

public class StructureCreator extends CreatorBlocks implements ICreatorBlock {

	boolean doReplaceAir;
	public int i=0;
	public int j=0;
	public int k=0;
	protected SchematicStructure struct;
	protected int speedUp=100;
	public String structureName;
	protected int id;
	public int generatedBlocks=0;
	public StructureCreator boundTo;

	protected StructureCreator(String name, World world, int i, int j, int k, boolean doReplaceAir, int id) {
		if(this instanceof StructureCreatorClient){
			CloneDetection.eventHandler.serverCreators.add(new StructureCreatorServer(name,i,j,k,doReplaceAir,id));
			((StructureCreator)CloneDetection.eventHandler.serverCreators.get(CloneDetection.eventHandler.serverCreators.size()-1)).boundTo=this;
			boundTo=((StructureCreator)CloneDetection.eventHandler.serverCreators.get(CloneDetection.eventHandler.serverCreators.size()-1));
		} else {
			this.world=world;
			this.x=i;
			this.y=j;
			this.z=k;
			this.doReplaceAir=doReplaceAir;
			this.id=id;
			init(name, doReplaceAir, !world.isRemote);
		}
	}
	
	protected void init(String structureName,boolean doPlaceAir, boolean server){
		this.structureName=structureName;
		SchematicStructure struct = new SchematicStructure(structureName+".structure", false);
	      struct.readFromFile();
	      if(!doPlaceAir){
	    	  struct.doNotReplaceAir();
	      }
	      struct.initSingleBlockPlacer(world, this.x,this.y,this.z);
	      this.struct=struct;
	      if(server){
	      registerStructure(id);
	      }
	}

	@Override
	public boolean run() {
		for(int i = 0; i<speedUp; i++){
			//System.out.println(i+", "+j+", "+k+", "+generatedBlocks);
		if(struct.placeBlock(this)){
			if(!world.isRemote){
				CloneDetection.eventHandler.postProcessors.add(struct);
			removeThisStructure();
			}
			return true;
	}
		generatedBlocks++;
		}
		registerStructure(id);
		return false;
	}
	
	protected void registerStructure(int at){
		//System.out.println("Registered "+at);
		if(!world.isRemote){
		PrintWriter writer;
		try {
			(new File(SavePaths.getSaveFolder()+Minecraft.getMinecraft().getIntegratedServer().getFolderName()+"/Structures")).mkdirs();
			writer = new PrintWriter(SavePaths.getSaveFolder()+Minecraft.getMinecraft().getIntegratedServer().getFolderName()+"/Structures/"+at+".txt", "UTF-8");
		
			writer.println(structureName);
			writer.println(doReplaceAir);
			writer.println(x);
			writer.println(y);
			writer.println(z);
			writer.println(i);
			writer.println(j);
			writer.println(k);
			writer.println(speedUp);
		
		writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	protected void removeThisStructure(){
		if(!world.isRemote){
		for(int i = 0; i<CloneDetection.eventHandler.creators.size(); i++){
			if(CloneDetection.eventHandler.creators.get(i) instanceof StructureCreator){
			if(((StructureCreator)CloneDetection.eventHandler.creators.get(i)).id>id){
				((StructureCreator)CloneDetection.eventHandler.creators.get(i)).id--;
			}}
		}
		new File(SavePaths.getSaveFolder()+Minecraft.getMinecraft().getIntegratedServer().getFolderName()+"/Structures/"+id+".txt").delete();
		System.out.println("Removed "+id);
		}
	}

	@Override
	public String toString() {
		return "StructureCreator [doReplaceAir=" + doReplaceAir + ", i=" + i + ", j=" + j + ", k=" + k + ", struct="
				+ struct + ", speedUp=" + speedUp + ", structureName=" + structureName + ", id=" + id
				+ ", generatedBlocks=" + generatedBlocks + ", boundTo=" + boundTo + "]";
	}


}
