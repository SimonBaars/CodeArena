package nl.sandersimon.clonedetection.minecraft.structureloader;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.sandersimon.clonedetection.CloneDetection;

public class BlockPlacer
{
	private World world;
	//private ArrayList<BlockPos> updatePos;
	//private ArrayList<IBlockState> updateState;
	boolean isLive;
	private ArrayList<IBlockState> specialBlocks = new ArrayList<IBlockState>();
	private ArrayList<BlockPos> specialBlockPos = new ArrayList<BlockPos>();
	private ArrayList<BlockPos> disabledPos = new ArrayList<BlockPos>();
	
	public BlockPlacer(World world, boolean isLive)
	{
		this.world = world;
		this.isLive=isLive;
		//this.updatePos = new ArrayList<BlockPos>();
		//this.updateState = new ArrayList<IBlockState>();
	}
	
	public void processSpecialBlocks(){
		for (int i = 0; i < this.specialBlocks.size(); i++)
		{
			add(specialBlocks.get(i), specialBlockPos.get(i), false);
			}
	}

	public boolean add(IBlockState blockState, BlockPos blockPos)
	{
		return add(blockState, blockPos, true);
	}
	
	public boolean add(IBlockState blockState, BlockPos blockPos, boolean doCheckSpecialBlock)
	{
		/*Field field;
		try {
			field = World.class.getDeclaredField("processingLoadedTiles");
			field.setAccessible(true);
	        Object value = field.get(world);
	        while((Boolean)value){
	        	value = field.get(world);
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*world.processingLoadedTiles.setAccessible(true);
		while(world.processingLoadedTiles){
			
		}*/
		for(BlockPos pos : disabledPos){
			if(pos.getX()==blockPos.getX() && pos.getY()==blockPos.getY() && pos.getZ()==blockPos.getZ()){
				disabledPos.remove(pos);
				return true;
			}
		}
		Block block = blockState.getBlock();
		if(doCheckSpecialBlock && isSpecialBlock(block)){
			if(isLive){
				return false;
			}
			if(block instanceof BlockDoor){
				BlockPos pos = new BlockPos(blockPos.getX(),blockPos.getY()+1,blockPos.getZ());
				world.setBlockState(blockPos, blockState.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 2);
		        world.setBlockState(pos, blockState.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2);
		        disabledPos.add(pos);
		        return true;
			} else if(block instanceof BlockBed){
                //world.setBlockState(blockPos, blockState.withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT), 11);
                return false;
			}
			//specialBlocks.add(blockState);
			//specialBlockPos.add(blockPos);
			//return false;
		}
		if(isLive && blockState.getMaterial()==Material.LAVA){
			return false;
		}
		boolean blockAdded = DropFuncBlock.setBlock(this.world, blockState, blockPos, false, isLive);
		//this.updatePos.add(blockPos);
		//this.updateState.add(blockState);
		this.world.markAndNotifyBlock(blockPos, this.world.getChunkFromBlockCoords(blockPos), this.world.getBlockState(blockPos), blockState, 3);
		if(world.isRemote){
			CloneDetection.eventHandler.lightUpdate.addClientProcess(blockPos);
		} else {
			CloneDetection.eventHandler.lightUpdate.addServerProcess(blockPos);
		}
		return blockAdded;
	}

	private boolean isSpecialBlock(Block block) {
		return (block instanceof BlockBanner || 
				block instanceof BlockBasePressurePlate ||
				block instanceof BlockBed ||
				block instanceof BlockBrewingStand ||
				block instanceof BlockButton ||
				block instanceof BlockCactus ||
				block instanceof BlockCake ||
				block instanceof BlockDoor ||
				block instanceof BlockFlowerPot ||
				block instanceof BlockLadder ||
				block instanceof BlockRailBase ||
				block instanceof BlockRedstoneComparator ||
				block instanceof BlockRedstoneTorch ||
				block instanceof BlockRedstoneWire ||
				block instanceof BlockSign ||
				block instanceof BlockBush ||
				block instanceof BlockTorch ||
				block instanceof BlockLever ||
				block instanceof BlockTripWireHook);
	}

	/*public void update()
	{
		for (int i = 0; i < this.updatePos.size(); i++)
		{
			this.world.markAndNotifyBlock(this.updatePos.get(i), this.world.getChunkFromBlockCoords(this.updatePos.get(i)), this.world.getBlockState(this.updatePos.get(i)), this.updateState.get(i), 3);
		}
	}*/
}
