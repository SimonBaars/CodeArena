package com.simonbaars.codearena.challenge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.minecraft.structureloader.SchematicStructure;
import com.simonbaars.codearena.model.MetricProblem;
import com.simonbaars.codearena.monster.CodeEntity;
import com.simonbaars.codearena.monster.codecreeper.EntityCodeCreeper;
import com.simonbaars.codearena.monster.codeskeleton.EntityCodeSkeleton;
import com.simonbaars.codearena.monster.codespider.EntityCodeSpider;
import com.simonbaars.codearena.monster.codezombie.EntityCodeZombie;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

public class CodeArena extends Challenges {
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
	private SchematicStructure checkStructure;
	private boolean doReplaceStuff = false;
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
		resetPlayer();
	}
	
	void initArena(){
		SchematicStructure structure = new SchematicStructure("arena");
		structure.readFromFile();
		sizex=structure.length;
		sizey=structure.height;
		sizez=structure.width;
		structure.process(serverWorld, worldIn, x+32, y, z+37);
	}
	
	void spawnMobs(int monsterId, int amount){
		amount=(amount*((wave/nWaves)+1))*numberOfPlayers;
		//for(int i = 0; i<amount; i++){
			//CodeEntity monster = getMonster(monsterId, serverWorld);
			//monster.setLocationAndAngles(cornerx+((int)(Math.random()*(fieldx-2)))+1, y+2, cornerz+((int)(Math.random()*(fieldz-2)))+1, 0, 0);
			//monster.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(monster)), (IEntityLivingData)null);
			//monster.spawnEntityInWorld(monster);
			//serverWorld.spawnEntity(monster);
			//activeMonsters.add(monster);
		//}
	}
	
	public void create(String metric, MetricProblem cloneClass) {
		CodeEntity monster = getMonster(serverWorld, cloneClass, metric);
		//System.out.println("Created "+monster.getRepresents());
		monster.setLocationAndAngles(cornerx+((int)(Math.random()*(fieldx-2)))+1, y+3, cornerz+((int)(Math.random()*(fieldz-2)))+1, 0, 0);
		monster.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(monster)), (IEntityLivingData)null);
		//monster.spawnEntityInWorld(monster);
		serverWorld.spawnEntity(monster);
		activeMonsters.add(monster);
	}

	private CodeEntity getMonster(World world, MetricProblem cloneClass, String metric) {
		switch(metric) {
			case "clones": return new EntityCodeSpider(world, cloneClass);
			case "unitcomplexity": return new EntityCodeZombie(world, cloneClass);
			case "unitinterfaces": return new EntityCodeSkeleton(world, cloneClass);
			default: return new EntityCodeCreeper(world, cloneClass);
		}
	}
	
	private EntityLiving getMonster(int monsterId, World world) {
		switch(monsterId){
			case 0: return new EntityZombie(world);
			case 1: return new EntitySpider(world);
			case 2: return new EntityBlaze(world);
			case 3: return new EntityCaveSpider(world);
			case 4: return new EntityCreeper(world);
			case 5: return new EntityEnderman(world);
			case 6: return new EntityEndermite(world);
			case 7: return new EntityGiantZombie(world);
			case 8: return new EntityGuardian(world);
			case 9: return new EntityMagmaCube(world);
			case 10: return new EntityPigZombie(world);
			case 11: return new EntitySilverfish(world);
			case 12: return new EntitySkeleton(world); /*skelly.setCurrentItemOrArmor(0, new ItemStack(Items.bow)); return skelly*/
			case 13: return new EntitySlime(world);
			case 14: return new EntityWitch(world);
			case 15: return new EntityWolf(world);
			case 16: EntityRabbit rabbit = new EntityRabbit(world); rabbit.setRabbitType(99); return rabbit;
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
		//System.out.println(x+", "+y+", "+z+", "+(fieldx+(2*howClose))+", "+(fieldy+(2*howClose)+3)+", "+(fieldz+(2*howClose)+2));
		return (x>=0 && x<=fieldx+(2*howClose) && y>=0 && y<=fieldy+(2*howClose) && z>=0 && z<=fieldz+(2*howClose)+2);
	}
	
	public boolean run() {
		ItemStack heldItem = Minecraft.getMinecraft().player.getHeldItemMainhand();
		if(heldItem.getItem() == Items.DIAMOND && !currentFilter.equals(heldItem.getDisplayName())) {
			currentFilter = heldItem.getDisplayName();
			for(CodeEntity e : activeMonsters) {
				/*if(currentFilter.equals(SHOW_ALL) || e.getRepresents().getPackage().equals(currentFilter)) {
					e.setInvisible(false);
				} else {
					e.setInvisible(true);
				}*/
				e.setInvisible(!currentFilter.equals(SHOW_ALL) && !e.getRepresents().getPackage().equals(currentFilter));
			}
		}
		return true;
	}
	
	private void removeWaterWorld() {
		// TODO Auto-generated method stub
		doReplaceStuff=true;
		placeBlocks(Blocks.AIR, cornerx+fieldx-1, y+2,cornerz+fieldz,fieldx,2,fieldz);
	}

	private void setWaterWorld() {
		// TODO Auto-generated method stub
		doReplaceStuff=false;
		placeBlocks(Blocks.WATER, cornerx+fieldx-1, y+2,cornerz+fieldz,fieldx,2,fieldz);
		for(int i  = 0; i<fieldx; i+=3){
			for(int j = 0; j<2; j++){
				for(int k  = 0; k<fieldz; k+=3){
					placeBlocks(Blocks.SAND, cornerx+fieldx-1-i, y+2+j,cornerz+fieldz-k,1,2,1);
				}
			}
		}
	}

	void register(){
		//System.out.println("Registered "+at);
		PrintWriter writer;
		try {
			(new File("saves/"+Minecraft.getMinecraft().getIntegratedServer().getFolderName())).mkdirs();
			writer = new PrintWriter("saves/"+Minecraft.getMinecraft().getIntegratedServer().getFolderName()+"/challenge.txt", "UTF-8");
			writer.println(x+32);
			writer.println(y-1);
			writer.println(z+37);
			writer.println(sizex);
			writer.println(sizey);
			writer.println(sizez);
		writer.close();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				
				if(codeEntity instanceof EntityCodeSpider) {
					float f = codeEntity.getRepresents().volume()*0.03F;
					e.setSizePublic(1.5F*f, 0.8F*f);
					codeEntity.setSizePublic(1.5F*f, 0.8F*f);
				} else if(codeEntity instanceof EntityCodeSkeleton) {
					float f = ((codeEntity.getRepresents().volume()*2)+4)*0.03F;
					e.setSizePublic(0.6F*f, 1.99F*f);
					codeEntity.setSizePublic(0.6F*f, 1.99F*f);
				} else if(codeEntity instanceof EntityCodeCreeper) {
					float f = ((codeEntity.getRepresents().volume()/1.5F)-18)*0.03F;
					e.setSizePublic(0.6F*f, 1.7F*f);
					codeEntity.setSizePublic(0.6F*f, 1.7F*f);
				} else if(codeEntity instanceof EntityCodeZombie) {
					float f = (codeEntity.getRepresents().volume()-13)*0.03F;
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