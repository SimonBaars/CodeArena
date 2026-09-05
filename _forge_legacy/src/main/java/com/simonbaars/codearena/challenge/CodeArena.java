package com.simonbaars.codearena.challenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.minecraft.structureloader.SchematicStructure;
import com.simonbaars.codearena.model.MetricProblem;
import com.simonbaars.codearena.monster.CodeEntity;
import com.simonbaars.codearena.monster.UsesCustomScaleFactors;
import com.simonbaars.codearena.monster.codecreeper.EntityCodeCreeper;
import com.simonbaars.codearena.monster.codeskeleton.AbstractCodeSkeleton;
import com.simonbaars.codearena.monster.codeskeleton.EntityCodeSkeleton;
import com.simonbaars.codearena.monster.codespider.EntityCodeSpider;
import com.simonbaars.codearena.monster.codezombie.EntityCodeZombie;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

public class CodeArena extends Challenges implements UsesCustomScaleFactors {
	int sizex;
	int sizey;
	int sizez;
	final int fieldx = 30;
	final int fieldy = 5;
	final int fieldz = 40;
	int cornerx;
	int cornerz;
	List<CodeEntity> activeMonsters = new ArrayList<>();
	Map<CodeEntity, CodeEntity> clientServerEntityMapping= new HashMap<>();
	int wave = 0;
	final int nWaves = 20;
	int ticks = 0;
	private String currentFilter = SHOW_ALL;
	
	
	public CodeArena(int x, int y, int z) {
		super(x,y,z,GameType.CREATIVE,EnumDifficulty.NORMAL);
		cornerx=x-15;
		cornerz=z-20;
		initArena();
		Minecraft.getMinecraft().player.inventory.currentItem = 0;
		for(EntityPlayerMP player : players){
			player.setPosition(x, y+3,z);
			player.setHealth(20);
			//items.add(Items.DIAMOND_SWORD);
			//items.add(Items.BOW);
			//for(int i = 0; i<3; i++)
			//	items.add(Items.ARROW);
		}
		ItemStack itemStackIn = new ItemStack(Items.DIAMOND, 1);
		itemStackIn.setStackDisplayName(SHOW_ALL);
		Minecraft.getMinecraft().player.inventory.addItemStackToInventory(itemStackIn);
		Minecraft.getMinecraft().player.inventoryContainer.detectAndSendChanges();
	}
	
	void initArena(){
		SchematicStructure structure = new SchematicStructure("arena");
		structure.readFromFile();
		sizex=structure.length;
		sizey=structure.height;
		sizez=structure.width;
		structure.process(serverWorld, worldIn, x+32, y, z+37);
	}

	public void create(ProblemType problem, MetricProblem cloneClass) {
		CodeEntity monster = getMonster(serverWorld, cloneClass, problem);
		//System.out.println("Created "+monster.getRepresents());
		monster.setLocationAndAngles(cornerx+((int)(Math.random()*(fieldx-2)))+1, y+3, cornerz+((int)(Math.random()*(fieldz-2)))+1, 0, 0);
		monster.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(monster)), (IEntityLivingData)null);
		//monster.spawnEntityInWorld(monster);
		serverWorld.spawnEntity(monster);
		activeMonsters.add(monster);
	}

	private CodeEntity getMonster(World world, MetricProblem cloneClass, ProblemType type) {
		switch(type) {
			case DUPLICATION: return new EntityCodeSpider(world, cloneClass);
			case UNITCOMPLEXITY: return new EntityCodeZombie(world, cloneClass);
			case UNITINTERFACESIZE: return new EntityCodeSkeleton(world, cloneClass);
			case UNITVOLUME: return new EntityCodeCreeper(world, cloneClass);
		}
		return null;
	}

	void destroy(){
		CloneDetection.get().setArena(null);
		placeBlocks(Blocks.AIR, x+32, y,z+38,sizex,sizey,sizez);
		for(int i = 0; i<activeMonsters.size(); i++){
			activeMonsters.get(i).setDead();
		}
		int amountOfEmeralds = getCurrentReward();
		for(EntityPlayerMP player : players){
			for(int i = amountOfEmeralds; i>0; i-=64) {
				player.inventory.addItemStackToInventory(new ItemStack(Items.EMERALD, i >= 64 ? 64 : i));
				player.inventoryContainer.detectAndSendChanges();
			}
		}
	}
	
	boolean closeToGameRoom(int howClose, int x, int y, int z){
		x = x-cornerx-howClose;
		y = y-this.y-howClose;
		z = z-cornerz-howClose;
		return (x>=0 && x<=fieldx+(2*howClose) && y>=0 && y<=fieldy+(2*howClose) && z>=0 && z<=fieldz+(2*howClose)+2);
	}
	
	public boolean run() {
		ItemStack heldItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
		if(heldItem.getItem() == Items.DIAMOND && !currentFilter.equals(heldItem.getDisplayName())) {
			currentFilter = heldItem.getDisplayName();
			for(CodeEntity e : activeMonsters) {
				e.setInvisible(!currentFilter.equals(SHOW_ALL) && !e.getRepresents().getPackage().equals(currentFilter));
			}
		}
		return true;
	}
	
	public void killSpider(MetricProblem cloneClass) {
		for(int i = 0; i<activeMonsters.size(); i++) {
			if(activeMonsters.get(i).getRepresents().equals(cloneClass)) {
				activeMonsters.get(i).setDead();
				activeMonsters.remove(i);
				return;
			}
		}
	}

	public MetricProblem getSpiderByPos(BlockPos pos) {
		for(int i = 0; i<activeMonsters.size(); i++) {
			if(activeMonsters.get(i).getPosition().equals(pos)) {
				return activeMonsters.get(i).getRepresents();
			}
		}
		
		System.out.println("Not found "+pos+ " for "+activeMonsters.stream().map(e -> e.getPosition().toString()).collect(Collectors.joining(",")));
		return null;
	}

	public MetricProblem findEntity(CodeEntity e) {
		if(clientServerEntityMapping.containsKey(e)) {
			return clientServerEntityMapping.get(e).getRepresents();
		}
		
		for(int i = 0; i<activeMonsters.size(); i++) {
			CodeEntity codeEntity = activeMonsters.get(i);
			if(codeEntity.getEntityId() == e.getEntityId()) {
				//System.out.println("Found!");
				clientServerEntityMapping.put(e, codeEntity);
				e.setRepresents(codeEntity.getRepresents());
				
				float f = getScaleFactor(codeEntity);
				if(codeEntity instanceof EntityCodeSpider) { //Code clones
					e.setSizePublic(1.5F*f, 0.8F*f);
					codeEntity.setSizePublic(1.5F*f, 0.8F*f);
				} else if(codeEntity instanceof AbstractCodeSkeleton) { // Unit interface size
					e.setSizePublic(0.6F*f, 1.99F*f);
					codeEntity.setSizePublic(0.6F*f, 1.99F*f);
				} else if(codeEntity instanceof EntityCodeCreeper) { // Unit volume
					e.setSizePublic(0.6F*f, 1.7F*f);
					codeEntity.setSizePublic(0.6F*f, 1.7F*f);
				} else if(codeEntity instanceof EntityCodeZombie) { // Unit complexity
					e.setSizePublic(0.6F*f, 1.95F*f);
					codeEntity.setSizePublic(0.6F*f, 1.95F*f);
				}
				return codeEntity.getRepresents();
			}
		}
		
		//System.out.println("Not found "+e.getPosition()+ " for "+activeMonsters.stream().map(i -> i.getPosition().toString()).collect(Collectors.joining(",")));
		return null;
	}

	public int getCurrentReward() {
		return displayScore.getScorePoints(); 
	}

	public void increaseScore() {
		increaseScore(1);
	}
}
