package nl.sandersimon.clonedetection.minecraft.structureloader;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import nl.sandersimon.clonedetection.CloneDetection;

public class BlockPlaceHandler {
	public static void placeBlocks(World worldIn, World serverWorld, Block block, int posx, int posy, int posz, int sizex, int sizey, int sizez){
		BlockPos pos;
		BlockPlacer blockPlacer = new BlockPlacer(serverWorld,false);
		BlockPlacer blockPlacer2 = new BlockPlacer(worldIn,false);
		for(int x = 0; x<sizex; x++){
			for(int y =0; y<sizey; y++){
				for(int z =0; z<sizez; z++){
					pos= new BlockPos(posx-x,posy+y,posz-z);
					blockPlacer.add(block.getDefaultState(), pos);
					blockPlacer2.add(block.getDefaultState(), pos);
				}
			}
		}
	}
	
	public static void placeBlock(Block block, int posx, int posy, int posz){
		BlockPos pos= new BlockPos(posx,posy,posz);
		setBlock(Minecraft.getMinecraft().world,pos, block.getDefaultState());
		setBlock(Minecraft.getMinecraft().getIntegratedServer().getEntityWorld(), pos, block.getDefaultState());
	}
	
	public static void placeBlocks(Block block, int posx, int posy, int posz, int sizex, int sizey, int sizez){
		World worldIn = Minecraft.getMinecraft().world;
		World serverWorld = Minecraft.getMinecraft().getIntegratedServer().getEntityWorld();
		BlockPos pos;
		for(int x = 0; x<sizex; x++){
			for(int y =0; y<sizey; y++){
				for(int z =0; z<sizez; z++){
					pos= new BlockPos(posx+x,posy+y,posz+z);
					setBlock(worldIn,pos, block.getDefaultState());
					setBlock(serverWorld, pos, block.getDefaultState());
				}
			}
		}
	}
	
	public static void placeBlock(World worldIn, World serverWorld, Block block, int posx, int posy, int posz){
		BlockPos pos= new BlockPos(posx,posy,posz);
		setBlock(worldIn,pos, block.getDefaultState());
		setBlock(serverWorld, pos, block.getDefaultState());
	}
	
	public static void setBlock(World world, BlockPos pos, IBlockState state){
		//if(addToMap(state, pos.getX(),pos.getY(),pos.getZ())){
		try{
			Chunk chunk = world.getChunkFromBlockCoords(pos);
			ExtendedBlockStorage storageArray = chunk.getBlockStorageArray()[pos.getY() >> 4];
			if (storageArray == null) storageArray = chunk.getBlockStorageArray()[pos.getY() >> 4] = new ExtendedBlockStorage(pos.getY() >> 4 << 4, !world.provider.isNether());

			if (storageArray.get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15).getBlock() != state.getBlock())
			{
				//IBlockState oldState = world.getBlockState(pos);
				storageArray.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
				if(world.isRemote){
					CloneDetection.get().eventHandler.lightUpdate.addClientProcess(pos);
				} else {
					CloneDetection.get().eventHandler.lightUpdate.addServerProcess(pos);
				}
				//world.checkLightFor(EnumSkyBlock.SKY, pos);
				//world.markBlockForUpdate(pos);
				/*if(world.isRemote){
				IMSM.eventHandler.lightUpdate.processes.add(pos);
				}*/
			}
		} catch (Exception e){
			//e.printStackTrace();
		}
	}
}
