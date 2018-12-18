package nl.sandersimon.clonedetection.minecraft.structureloader;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class LightUpdateCheck {
	private World worldIn;
	private World serverWorld;
	private ArrayList<BlockPos> serverProcesses = new ArrayList<BlockPos>();
	public ArrayList<BlockPos> clientProcesses = new ArrayList<BlockPos>();
	
	
    public LightUpdateCheck(World worldIn, World serverWorld){
    	this.worldIn=worldIn;
    	this.serverWorld=serverWorld;
    }
    
    public void addClientProcess(BlockPos pos){
    	clientProcesses.add(pos);
    }
    
    public void addServerProcess(BlockPos pos){
    	serverProcesses.add(pos);
    }
    
	public void runClient() {
		for(int i = clientProcesses.size()-1; i>=0; i--){
			if(clientProcesses.get(i)!=null){
				worldIn.checkLightFor(EnumSkyBlock.SKY, clientProcesses.get(i));
			}
			clientProcesses.remove(i);
		}	
	}
	public void runServer() {
		for(int i = serverProcesses.size()-1; i>=0; i--){
			if(serverProcesses.get(i)!=null){
				serverWorld.checkLightFor(EnumSkyBlock.SKY, serverProcesses.get(i));
			}
			serverProcesses.remove(i);
		}	
	}
}
