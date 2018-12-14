package nl.sandersimon.clonedetection.challenge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.minecraft.structureloader.SchematicStructure;
import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.monster.CodeEntity;
import nl.sandersimon.clonedetection.monster.codespider.EntityCodeSpider;

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
	int wave = 0;
	final int nWaves = 20;
	private SchematicStructure checkStructure;
	private boolean doReplaceStuff = false;
	int ticks = 0;
	
	public CodeArena(int x, int y, int z) {
		super(x,y,z,GameType.CREATIVE,EnumDifficulty.NORMAL);
		cornerx=x-15;
		cornerz=z-20;
		showScore();
		initArena();
		for(EntityPlayerMP player : players){
			player.setPosition(x, y+2,z);
			player.setHealth(20);
		}
		resetPlayer();
	}
	
	void initArena(){
		SchematicStructure structure = new SchematicStructure("arena");
		structure.readFromFile();
		sizex=structure.length;
		sizey=structure.height;
		sizez=structure.width;
		structure.process(serverWorld, worldIn, x+32, y-1, z+37);
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
	
	public void create(CloneClass cloneClass, int type) {
		CodeEntity monster = getMonster(worldIn, cloneClass, type, cloneClass.size());
		monster.setLocationAndAngles(cornerx+((int)(Math.random()*(fieldx-2)))+1, y+2, cornerz+((int)(Math.random()*(fieldz-2)))+1, 0, 0);
		monster.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(monster)), (IEntityLivingData)null);
		//monster.spawnEntityInWorld(monster);
		worldIn.spawnEntity(monster);
		activeMonsters.add(monster);
	}

	private CodeEntity getMonster(World world, CloneClass cloneClass, int type, int size) {
		switch(type){
		case 1: return new EntityCodeSpider(world, cloneClass, size);
		//case 2: return new EntityBlaze(world);
		//case 3: return new EntityCaveSpider(world);
		}
		return null;
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
		placeBlocks(Blocks.AIR, x+32, y-1,z+38,sizex,sizey,sizez);
		for(int i = 0; i<activeMonsters.size(); i++){
			activeMonsters.get(i).setDead();
		}
	}
	
	boolean closeToGameRoom(int howClose, int x, int y, int z){
		x = x-cornerx-howClose;
		y = y-this.y-1-howClose;
		z = z-cornerz-howClose;
		//System.out.println(x+", "+y+", "+z+", "+(fieldx+(2*howClose))+", "+(fieldy+(2*howClose)+3)+", "+(fieldz+(2*howClose)+2));
		return (x>=0 && x<=fieldx+(2*howClose) && y>=0 && y<=fieldy+(2*howClose) && z>=0 && z<=fieldz+(2*howClose)+2);
	}
	
	public boolean run() {
		for(int i = 0; i<activeMonsters.size(); i++){
			if(activeMonsters.get(i).isDead){
				activeMonsters.remove(i);
				increaseScore();
				i--;
			} else if(activeMonsters.get(i).posY>y+4){
				activeMonsters.get(i).setDead();
			}
		}
		showScore();
		return true;
	}
	
	public boolean runOld(){
		waitTime=1000;
		ticks++;
		if(ticks==25 || !doReplaceStuff || activeMonsters.size()==0){
			int realWave = wave%nWaves;
			switch(realWave){
			case 0: items.clear(); items.add(Items.WOODEN_SWORD); spawnMobs(16,2); break;
			case 1: spawnMobs(12,2); break;
			case 2: spawnMobs(13,4); break;
			case 3: spawnMobs(11,10); break;
			case 4: spawnMobs(15,5); break;
			case 5: spawnMobs(0,20); break;
			case 6: spawnMobs(4,5); break;
			case 7: items.add(Items.STONE_SWORD); spawnMobs(8, 2); break;
			case 8: spawnMobs(9, 10); break;
			case 9: spawnMobs(2, 5); break;
			case 10: spawnMobs(10,6); break;
			case 11: spawnMobs(1,20); break;
			case 12: spawnMobs(11,50); break;
			case 13: items.add(Items.IRON_SWORD); spawnMobs(6,100); break;
			case 14: spawnMobs(9, 15); spawnMobs(13, 15); break;
			case 15: spawnMobs(10,15); break;
			case 16: items.add(Items.DIAMOND_SWORD); spawnMobs(0,50); break;
			case 17: spawnMobs(16,60); break;
			case 18: spawnMobs(12,20); break;
			case 19: int[] finalRound = {0,1,2,4,6,9,10,11,12,13,15,16}; items.add(Items.FLINT_AND_STEEL); for(int i = 0; i<finalRound.length; i++){ spawnMobs(finalRound[i],4); }
			} 
			wave++;
			doReplaceStuff=true;
			ticks=0;
		}
		serverWorld.setWorldTime(14000);
		if(Minecraft.getMinecraft().player!=null){
			if(doReplaceStuff){
				checkStructure.process(serverWorld, worldIn, cornerx+fieldx, y-1, cornerz+fieldz);	
			}
		if(resetPlayer()){
			return true;
		}
			for(int i = 0; i<activeMonsters.size(); i++){
				if(activeMonsters.get(i).isDead){
					activeMonsters.remove(i);
					increaseScore();
					i--;
				} else if(activeMonsters.get(i).posY>y+4){
					activeMonsters.get(i).setDead();
				}
			}
		showScore();
		waitTime--;
		
		
		register();
		}
		return false;
	}
	
	private void removeWaterWorld() {
		// TODO Auto-generated method stub
		doReplaceStuff=true;
		placeBlocks(Blocks.AIR, cornerx+fieldx-1, y+1,cornerz+fieldz,fieldx,2,fieldz);
	}

	private void setWaterWorld() {
		// TODO Auto-generated method stub
		doReplaceStuff=false;
		placeBlocks(Blocks.WATER, cornerx+fieldx-1, y+1,cornerz+fieldz,fieldx,2,fieldz);
		for(int i  = 0; i<fieldx; i+=3){
			for(int j = 0; j<2; j++){
				for(int k  = 0; k<fieldz; k+=3){
					placeBlocks(Blocks.SAND, cornerx+fieldx-1-i, y+1+j,cornerz+fieldz-k,1,2,1);
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
}
