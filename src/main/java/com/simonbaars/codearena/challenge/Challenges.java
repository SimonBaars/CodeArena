package com.simonbaars.codearena.challenge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.common.SavePaths;
import com.simonbaars.codearena.minecraft.structureloader.BlockPlaceHandler;

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
		Minecraft.getInstance().getIntegratedServer().setDifficultyForAllWorlds(defDifficulty);
		//Challenge.eventHandler.previousTick = System.currentTimeMillis();
		this.worldIn=Minecraft.getInstance().world;
		this.serverWorld=Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension);
		for(int i = 0; i<Minecraft.getInstance().player.inventory.mainInventory.size(); i++){
			oldInventory.add(Minecraft.getInstance().player.inventory.mainInventory.get(i));
		}
		for(int i = 0; i<Minecraft.getInstance().player.inventory.armorInventory.size(); i++){
			oldInventory.add(Minecraft.getInstance().player.inventory.armorInventory.get(i));
		}
		Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).getGameRules().setOrCreateGameRule("doMobSpawning", "false");
		//Minecraft.getInstance().player.sendChatMessage("The challenge has started!");
		numberOfPlayers = Minecraft.getInstance().getIntegratedServer().getCurrentPlayerCount();
		if(numberOfPlayers==0){
			System.out.println("Something went wrong while initializing players...");
		}
		players = new EntityPlayerMP[numberOfPlayers];
		int i = 0;
		for(Object player : Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).playerEntities){
			players[i] = (EntityPlayerMP)player;
			this.oldGameType=Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).getWorldInfo().getGameType();//players[i].theItemInWorldManager.getGameType();
			alivePlayers.add(players[i]);
			players[i].setGameType(defGameType);
			Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).getWorldInfo().setGameType(defGameType);
			i++;
		}
		lastTickTime=System.currentTimeMillis();
		//((EntityPlayerMP)Minecraft.getInstance().getIntegratedServer().worldServerForDimension(0).getPlayerEntityByName(Minecraft.getInstance().player.getName())).setGameType(GameType.ADVENTURE);
		scoreBoard = Minecraft.getInstance().world.getScoreboard().addScoreObjective("Code Duplication", IScoreCriteria.DUMMY);
		scoreBoard.setRenderType(EnumRenderType.INTEGER);
		scoreBoard.getScoreboard().setObjectiveInDisplaySlot(Scoreboard.getObjectiveDisplaySlotNumber("sidebar"), scoreBoard);
		displayScore = scoreBoard.getScoreboard().getOrCreateScore("Score", scoreBoard);
		displayScore.setScorePoints(0);
	}

	boolean withinGameRoom(int x, int y, int z){
		return closeToGameRoom(0,x,y,z);
	}

	abstract boolean closeToGameRoom(int howClose, int x, int y, int z);

	public void removeThisChallenge(){
		if(Minecraft.getInstance().player!=null){
			for(int i = 0; i<players.length; i++){
				players[i].inventory.clear();
				players[i].setGameType(oldGameType);
				int j;
				for(j = 0; j<Minecraft.getInstance().player.inventory.mainInventory.size(); j++){
					players[i].inventory.mainInventory.set(j, oldInventory.get(j));
				}
				for(int k = 0; k<Minecraft.getInstance().player.inventory.armorInventory.size(); k++){
					players[i].inventory.armorInventory.set(k, oldInventory.get(j));
					j++;
				}
			}
			int j;
			for(j = 0; j<Minecraft.getInstance().player.inventory.mainInventory.size(); j++){
				Minecraft.getInstance().player.inventory.mainInventory.set(j, oldInventory.get(j));
			}
			for(int k = 0; k<Minecraft.getInstance().player.inventory.armorInventory.size(); k++){
				Minecraft.getInstance().player.inventory.armorInventory.set(k, oldInventory.get(j));
				j++;
			}
			try{
				new File(SavePaths.getSaveFolder()+Minecraft.getInstance().getIntegratedServer().getFolderName()+"/challenge.txt").delete();
			} catch (Exception e){

			}
			scoreBoard.getScoreboard().removeObjective(scoreBoard);
			CloneDetection.get().getProblemScores().clear();
			//scoreBoard.getScoreboard().func_96519_k(scoreBoard);
			Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).getGameRules().setOrCreateGameRule("doMobSpawning", "true");
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
		EntityPlayerMP deadPlayer = (EntityPlayerMP)  Minecraft.getInstance().getIntegratedServer().getWorld(Minecraft.getInstance().player.dimension).getPlayerEntityByName(entityIn.getName());
		if(alivePlayers.contains(deadPlayer)){
			Minecraft.getInstance().player.sendChatMessage("It's Game Over for "+entityIn.getName()+"!");
			Minecraft.getInstance().player.sendChatMessage(entityIn.getName()+" has ended with a score of "+displayScore.getScorePoints());
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
