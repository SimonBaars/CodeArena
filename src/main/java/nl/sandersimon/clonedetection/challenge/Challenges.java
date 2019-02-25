package nl.sandersimon.clonedetection.challenge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.IScoreCriteria.EnumRenderType;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.minecraft.structureloader.BlockPlaceHandler;

public abstract class Challenges {
	World worldIn;
	World serverWorld;
	protected int x,y,z;
	EntityPlayerMP[] players;
	List<EntityPlayerMP> alivePlayers = new ArrayList<>();
	List<EntityPlayerMP> deadPlayers = new ArrayList<>();
	List<Item> items = new ArrayList<>();
	public long lastTickTime = 0;
	GameType defGameType;
	public int waitTime = 2000;
	public int numberOfPlayers;
	GameType oldGameType;
	EnumDifficulty defDifficulty;
	ArrayList<ItemStack> oldInventory = new ArrayList<>();
	Score displayScore;
	ScoreObjective scoreBoard;
	
	protected static String SHOW_ALL = "Show All Packages";

	public Challenges(int x, int y, int z, GameType defGameType, EnumDifficulty defDifficulty){
		this.defGameType=defGameType;
		this.x=x;
		this.y=y;
		this.z=z;
		this.defDifficulty=defDifficulty;
		CloneDetection.get().eventHandler.challenge=this;
		Minecraft.getMinecraft().getIntegratedServer().setDifficultyForAllWorlds(defDifficulty);
		//Challenge.eventHandler.previousTick = System.currentTimeMillis();
		this.worldIn=Minecraft.getMinecraft().world;
		this.serverWorld=Minecraft.getMinecraft().getIntegratedServer().getEntityWorld();
		for(int i = 0; i<Minecraft.getMinecraft().player.inventory.mainInventory.size(); i++){
			oldInventory.add(Minecraft.getMinecraft().player.inventory.mainInventory.get(i));
		}
		for(int i = 0; i<Minecraft.getMinecraft().player.inventory.armorInventory.size(); i++){
			oldInventory.add(Minecraft.getMinecraft().player.inventory.armorInventory.get(i));
		}
		for(int i = 0; i<Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().loadedEntityList.size(); i++){
			if(Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().loadedEntityList.get(i) instanceof EntityCreature || Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().loadedEntityList.get(i) instanceof EntityItem){
				((Entity)Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().loadedEntityList.get(i)).setDead();
				//Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().removeEntity((Entity) Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().loadedEntityList.get(i));
			}
		}
		Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().getGameRules().setOrCreateGameRule("doMobSpawning", "false");
		Minecraft.getMinecraft().player.sendChatMessage("The challenge has started!");
		numberOfPlayers = Minecraft.getMinecraft().getIntegratedServer().getCurrentPlayerCount();
		if(numberOfPlayers==0){
			System.out.println("Something went wrong while initializing players...");
		}
		players = new EntityPlayerMP[numberOfPlayers];
		int i = 0;
		for(Object player : Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().playerEntities){
			players[i] = (EntityPlayerMP)player;
			this.oldGameType=Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().getWorldInfo().getGameType();//players[i].theItemInWorldManager.getGameType();
			alivePlayers.add(players[i]);
			players[i].setGameType(defGameType);
			Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().getWorldInfo().setGameType(defGameType);
			i++;
		}
		lastTickTime=System.currentTimeMillis();
		//((EntityPlayerMP)Minecraft.getMinecraft().getIntegratedServer().worldServerForDimension(0).getPlayerEntityByName(Minecraft.getMinecraft().player.getName())).setGameType(GameType.ADVENTURE);
		scoreBoard = Minecraft.getMinecraft().world.getScoreboard().addScoreObjective("Code Duplication", IScoreCriteria.DUMMY);
		scoreBoard.setRenderType(EnumRenderType.INTEGER);
		scoreBoard.getScoreboard().setObjectiveInDisplaySlot(Scoreboard.getObjectiveDisplaySlotNumber("sidebar"), scoreBoard);
		displayScore = scoreBoard.getScoreboard().getOrCreateScore("Score", scoreBoard);
		displayScore.setScorePoints(0);
		//System.out.println(Challenge.highscores[getChallengeNum()-1]+", "+(getChallengeNum()-1));
	}

	public boolean resetPlayer(){
		if(y>150){
			Minecraft.getMinecraft().player.sendChatMessage("You cannot create this challenge this high...");
			removeThisChallenge();
			return true;
		}
		if(Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().getDifficulty()!=defDifficulty){
			Minecraft.getMinecraft().player.sendChatMessage("Please do not change the difficulty...");
			removeThisChallenge();
			return true;
		}
		for(EntityPlayerMP player : players){
			player.isAirBorne=false;
			//if(player.inventory.getCurrentItem()==null || player.inventory.getCurrentItem().getItem()!=Items.BOW){
				player.inventory.clear();
				for(Item item : items){
					player.inventory.addItemStackToInventory(new ItemStack(item, item.getItemStackLimit()));
				};
				player.inventoryContainer.detectAndSendChanges();
			//}
			/*for(int i = 0; i<Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().loadedEntityList.size(); i++){
			if(Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().loadedEntityList.get(i) instanceof EntityWitch){
				((Entity)Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().loadedEntityList.get(i)).setDead();
				//Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().removeEntity((Entity) Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().loadedEntityList.get(i));
			}
		}*/
			player.getFoodStats().setFoodLevel(20);
			if(alivePlayers.contains(player) && Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().getWorldInfo().getGameType()!=defGameType){
				Minecraft.getMinecraft().player.sendChatMessage("You cannot do this challenge in any other gamemode than survival...");
				removeThisChallenge();
				return true;
			} 
			if(deadPlayers.contains(player) && Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().getWorldInfo().getGameType()!=GameType.SPECTATOR){
				Minecraft.getMinecraft().player.sendChatMessage("You cannot be in any other gamemode than SPECTATOR now...");
				removeThisChallenge();
				return true;
			}
			if(player.getActivePotionEffects().size()>0){
				Minecraft.getMinecraft().player.sendChatMessage("You cannot do this challenge when potion effects are active");
				removeThisChallenge();
				return true;
			}
			if(alivePlayers.contains(player) && !withinGameRoom((int)player.posX, (int)player.posY, (int)player.posZ)){
				System.out.println("You left the gameroom? (this might be by error)");
				endChallenge(player);
				return true;
			}
		}
		/*if(Minecraft.getMinecraft().player.inventory.getCurrentItem()==null || Minecraft.getMinecraft().player.inventory.getCurrentItem().getItem()!=Items.BOW){
			Minecraft.getMinecraft().player.inventory.clear();
			for(Item item : items){
				Minecraft.getMinecraft().player.inventory.addItemStackToInventory(new ItemStack(item, item.getItemStackLimit()));
			}
			Minecraft.getMinecraft().player.inventoryContainer.detectAndSendChanges();
		}*/
		Minecraft.getMinecraft().player.inventory.clear();
		ItemStack itemStackIn = new ItemStack(Items.DIAMOND, 1);
		itemStackIn.setStackDisplayName(SHOW_ALL);
		Minecraft.getMinecraft().player.inventory.addItemStackToInventory(new ItemStack(Items.BOW, 1));
		Minecraft.getMinecraft().player.inventory.addItemStackToInventory(new ItemStack(Items.ARROW, 64));
		Minecraft.getMinecraft().player.inventory.addItemStackToInventory(itemStackIn);
		Minecraft.getMinecraft().player.inventoryContainer.detectAndSendChanges();
		if(numberOfPlayers!=Minecraft.getMinecraft().getIntegratedServer().getCurrentPlayerCount()){
			Minecraft.getMinecraft().player.sendChatMessage("No players may leave or join the game while a challenge is running.");
			removeThisChallenge();
			return true;
		}
		/*if(Minecraft.getMinecraft().getIntegratedServer().getCurrentPlayerCount()>1){
			Minecraft.getMinecraft().player.addChatMessage(new TextComponentString("You cannot do this challenge when other players are online"));
			removeThisChallenge();
		}*/
		return false;
	}

	boolean withinGameRoom(int x, int y, int z){
		return closeToGameRoom(0,x,y,z);
	}

	abstract boolean closeToGameRoom(int howClose, int x, int y, int z);

	public void removeThisChallenge(){
		if(Minecraft.getMinecraft().player!=null){
			for(int i = 0; i<players.length; i++){
				players[i].inventory.clear();
				players[i].setGameType(oldGameType);
				int j;
				for(j = 0; j<Minecraft.getMinecraft().player.inventory.mainInventory.size(); j++){
					players[i].inventory.mainInventory.set(j, oldInventory.get(j));
				}
				for(int k = 0; k<Minecraft.getMinecraft().player.inventory.armorInventory.size(); k++){
					players[i].inventory.armorInventory.set(k, oldInventory.get(j));
					j++;
				}
			}
			int j;
			for(j = 0; j<Minecraft.getMinecraft().player.inventory.mainInventory.size(); j++){
				Minecraft.getMinecraft().player.inventory.mainInventory.set(j, oldInventory.get(j));
			}
			for(int k = 0; k<Minecraft.getMinecraft().player.inventory.armorInventory.size(); k++){
				Minecraft.getMinecraft().player.inventory.armorInventory.set(k, oldInventory.get(j));
				j++;
			}
			try{
				new File(SavePaths.getSaveFolder()+Minecraft.getMinecraft().getIntegratedServer().getFolderName()+"/challenge.txt").delete();
			} catch (Exception e){

			}
			scoreBoard.getScoreboard().removeObjective(scoreBoard);
			//scoreBoard.getScoreboard().func_96519_k(scoreBoard);
			Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().getGameRules().setOrCreateGameRule("doMobSpawning", "true");
			destroy();
		}
		CloneDetection.get().eventHandler.challenge=null;
	}

	abstract void destroy();

	//abstract boolean addToMap(IBlockState state, int x,int y, int z);

	public void placeBlocks(Block block, int posx, int posy, int posz, int sizex, int sizey, int sizez){
		//System.out.println("PLACING "+block+" BETWEEN x "+posx+", y "+posy+", z "+posz+", sizex "+sizex+", sizey "+sizey+", sizez "+sizez);
		BlockPlaceHandler.placeBlocks(worldIn, serverWorld, block, posx, posy, posz, sizex, sizey, sizez);
	}

	//abstract void register(int x, int y, int z);


	abstract public boolean run();

	public void endChallenge(EntityPlayer entityIn) {
		EntityPlayerMP deadPlayer = (EntityPlayerMP)  Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().getPlayerEntityByName(entityIn.getName());
		if(alivePlayers.contains(deadPlayer)){
			Minecraft.getMinecraft().player.sendChatMessage("It's Game Over for "+entityIn.getName()+"!");
			Minecraft.getMinecraft().player.sendChatMessage(entityIn.getName()+" has ended with a score of "+displayScore.getScorePoints());
			alivePlayers.remove(deadPlayer);
			deadPlayers.add(deadPlayer);
		}
		if(alivePlayers.isEmpty()){
			removeThisChallenge();
		} else {
			deadPlayer.setGameType(GameType.SPECTATOR);
		}
	}

	public void endChallengeForAllPlayers() {
		for(int i = 0; i<players.length; i++){
			endChallenge(players[i]);
		}
	}

	public ScoreObjective getScoreBoard() {
		return scoreBoard;
	}

	public void setScoreBoard(ScoreObjective scoreBoard) {
		this.scoreBoard = scoreBoard;
	}
	
	public void increaseScore(int scoreGain) {
		displayScore.increaseScore(scoreGain);
	}
}
