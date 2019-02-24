package nl.sandersimon.clonedetection.minecraft.structureloader;

import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import nl.sandersimon.clonedetection.CloneDetection;

public class DropFuncBlock
{
	public static boolean setBlock(World world, IBlockState state, BlockPos pos, boolean update, boolean isLive)
	{
		 setBlock(world, state, pos, null, update, isLive);
		 return true;
	}

	private static boolean setBlock(World world, IBlockState state, BlockPos pos, NBTTagCompound tileEntity, boolean update, boolean isLive)
	{
		try{
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		//Chunk chunkClient = Minecraft.getMinecraft().theWorld.getChunkFromBlockCoords(pos);
		ExtendedBlockStorage storageArray = chunk.getBlockStorageArray()[pos.getY() >> 4];
		//System.out.println(pos.getX()+", "+pos.getY()+", "+pos.getZ());
		if (storageArray == null) storageArray = chunk.getBlockStorageArray()[pos.getY() >> 4] = new ExtendedBlockStorage(pos.getY() >> 4 << 4, !world.provider.isNether());
		
		if (storageArray.get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15).getBlock() != state.getBlock() || state.getBlock() instanceof BlockColored)
		{
			IBlockState oldState = world.getBlockState(pos);
			//storageArray.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
			//chunkClient.getBlockStorageArray()[pos.getY() >> 4].set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
			storageArray.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
			
			//world.setBlockState(pos, state);
			//int timesFaster=1;
			//if(!isLive || IMSM.eventHandler.lightUpdate.getProcessesSize()<200 /*&& pos.getX()%timesFaster==0 && pos.getY()%timesFaster==0 && pos.getZ()%timesFaster==0*/){
				//if(!isLive || IMSM.eventHandler.lightUpdate.getProcessesSize()<200){
				//if(!world.isRemote){
						//IMSM.eventHandler.lightUpdate.processes.add(pos);
				//}
				//}
				//world.checkLight(pos);
				//world.setLightFor(EnumSkyBlock.SKY, pos, 15);
				//world.setLightFor(EnumSkyBlock.BLOCK, pos, 15);
				//System.out.println((Minecraft.getMinecraft().theWorld==world)+"=="+(Minecraft.getMinecraft().getIntegratedServer().worldServerForDimension(0)==world));
			//}
			//world.notifyLightSet(pos);
				//world.checkLight(pos);
			//world.checkLightFor(EnumSkyBlock.SKY, pos);
			//if(world.isRemote){
			//	IMSM.eventHandler.lightUpdate.addClientProcess(pos);
			//} else {
			CloneDetection.get().eventHandler.lightUpdate.addServerProcess(pos);
				//IMSM.eventHandler.lightUpdate.addClientProcess(pos);
			//}
			/*if(world.isDaytime()){
			world.setLightFor(EnumSkyBlock.BLOCK, pos, 15);world.setLightFor(EnumSkyBlock.SKY, pos, 15);
			} else {
				Block block = state.getBlock();
				if(block instanceof BlockBeacon || block instanceof BlockFire || block instanceof BlockGlowstone || block instanceof BlockTorch || block instanceof BlockRedstoneDiode || block instanceof BlockRedstoneLight || block instanceof BlockPortal){
world.checkLight(pos);
			}else {
				world.setLightFor(EnumSkyBlock.BLOCK, pos, 4);world.setLightFor(EnumSkyBlock.SKY, pos, 4);
			}
			}*/
				//world.setBlockState(pos, oldState)
				chunk.setModified(true);
			//chunkClient.setChunkModified();
			world.markBlockRangeForRenderUpdate(pos,pos);//TODO: Stil a hack
			//Minecraft.getMinecraft().theWorld.markBlockRangeForRenderUpdate(pos,pos);//TODO: Stil a hack
			/*if(world.isRemote){
			//world.checkLight(pos);
				world.setLightFor(EnumSkyBlock.SKY, pos, 15);
				world.setLightFor(EnumSkyBlock.BLOCK, pos, 15);
			}*/
			//world.setLightFor(EnumSkyBlock.BLOCK, pos, 15);
			/*if (update || state.getBlock()!=Blocks.air)*/ world.markAndNotifyBlock(pos, chunk, state, oldState, 3);
			//Minecraft.getMinecraft().theWorld.markAndNotifyBlock(pos, chunk, state, oldState, 3);
		}

		if (tileEntity != null && state.getBlock().hasTileEntity(state) && !isLive)
		{
			world.removeTileEntity(pos);
			BlockPos chunkPos = new BlockPos(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
			TileEntity blockTileEntity = chunk.getTileEntity(chunkPos, Chunk.EnumCreateEntityType.CHECK);

			blockTileEntity = state.getBlock().createTileEntity(world, state);
			blockTileEntity.readFromNBT(tileEntity);
			world.setTileEntity(pos, blockTileEntity);
			blockTileEntity.updateContainingBlockInfo();
		}
		} catch (Exception e){
			return false;
		}
		return true;
	}

	public static synchronized void setTileEntity(World world, IBlockState state, BlockPos pos, NBTTagCompound tileEntity)
	{
		if (tileEntity != null && state.getBlock().hasTileEntity(state))
		{
			Chunk chunk = world.getChunkFromBlockCoords(pos);
			ExtendedBlockStorage storageArray = chunk.getBlockStorageArray()[pos.getY() >> 4];
			if (storageArray == null) storageArray = chunk.getBlockStorageArray()[pos.getY() >> 4] = new ExtendedBlockStorage(pos.getY() >> 4 << 4, !world.provider.isNether());

			world.removeTileEntity(pos);
			BlockPos chunkPos = new BlockPos(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
			TileEntity blockTileEntity = chunk.getTileEntity(chunkPos, Chunk.EnumCreateEntityType.CHECK);

			blockTileEntity = state.getBlock().createTileEntity(world, state);
			blockTileEntity.readFromNBT(tileEntity);
			world.setTileEntity(pos, blockTileEntity);
			blockTileEntity.updateContainingBlockInfo();
		}
	}
}
