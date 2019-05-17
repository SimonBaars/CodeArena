package com.simonbaars.codearena;

import java.awt.event.WindowAdapter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simonbaars.codearena.challenge.CodeArena;
import com.simonbaars.codearena.common.Commons;
import com.simonbaars.codearena.common.ResourceCommons;
import com.simonbaars.codearena.common.SavePaths;
import com.simonbaars.codearena.editor.CodeEditor;
import com.simonbaars.codearena.minecraft.CDEventHandler;
import com.simonbaars.codearena.minecraft.gui.CloneMenuKey;
import com.simonbaars.codearena.minecraft.gui.EndChallengeGUI;
import com.simonbaars.codearena.minecraft.gui.GUISetupCloneFinding;
import com.simonbaars.codearena.minecraft.proxy.CommonProxy;
import com.simonbaars.codearena.model.MetricProblem;
import com.simonbaars.codearena.model.ProblemScore;
import com.simonbaars.codearena.thread.ProblemDetectionThread;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = CloneDetection.MODID, name = CloneDetection.NAME, version = CloneDetection.VERSION, dependencies = "required-after:forge@[13.19.0.2129,)", useMetadata = true)
public class CloneDetection
{
	public static final String MODID = "clonedetection";
	public static final String NAME = "Clone Detection";
	public static final String VERSION = "1.0";
	
	public final CDEventHandler eventHandler = new CDEventHandler();

	@Mod.Instance
	private static CloneDetection cloneDetection;
	private final Map<String, List<MetricProblem>> problems = new HashMap<>();
	
	public Map<String, List<MetricProblem>> getProblems() {
		return problems;
	}

	private final List<ProblemScore> problemScores = new ArrayList<>();
	
	public List<ProblemScore> getProblemScores() {
		return problemScores;
	}

	@SidedProxy(clientSide = "nl.sandersimon.clonedetection.minecraft.proxy.ClientProxy", serverSide = "nl.sandersimon.clonedetection.minecraft.proxy.ServerProxy")
	public static CommonProxy proxy;
	
	private CodeArena arena;
	public List<CodeEditor> openEditors = new ArrayList<>();
	
	public static int dialoge;
	public static final List<String> packages = new ArrayList<>();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		if(proxy != null)
			proxy.preInit(event);
		cloneDetection = this;
		ResourceCommons.extractResources();
		//RenderingRegistry.registerEntityRenderingHandler(EntityCodeSpider.class, new CodeSpiderFactory());
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		event.registerServerCommand(new CloneCommand());
		event.registerServerCommand(new EndCommand());
	}

	public static CloneDetection get() {
		return cloneDetection;
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		if(proxy != null) {
			proxy.init(event);
			FMLCommonHandler.instance().bus().register(eventHandler);
			MinecraftForge.EVENT_BUS.register(new com.simonbaars.codearena.minecraft.ForgeEventHandler());
			NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
			new CloneMenuKey().registerRenderers();
		}
	}

	@Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        if(proxy != null)	
        	proxy.postInit(e);
    }

	public CodeArena getArena() {
		return arena;
	}

	public void setArena(CodeArena arena) {
		this.arena = arena;
	}
	
	public void initScoreboards() {
		ScoreObjective scoreBoard = arena.getScoreBoard();
		for(ProblemScore s : problemScores)
			s.setScorePoints(0);
		for(ProblemScore score : problemScores)
			score.setScore(scoreBoard);
	}

	public int perc(int total, int partOfTotal) {
		return (int) Math.round((((double)partOfTotal / (double)total) * 100.0));
	}

	public void closeAllEditors() {
		closeAllEditorsExcept(null);
	}
	
	public static class GuiHandler implements IGuiHandler {

		@Override
		public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			if (id == GUISetupCloneFinding.GUIID)
				return new GUISetupCloneFinding.GuiContainerMod(world, x, y, z, player);
			else if (id == EndChallengeGUI.GUIID)
				return new EndChallengeGUI.GuiContainerMod(world, x, y, z, player);
			return null;
		}

		@Override
		public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			if (id == GUISetupCloneFinding.GUIID)
				return new GUISetupCloneFinding.GuiWindow(world, x, y, z, player);
			else if (id == EndChallengeGUI.GUIID)
				return new EndChallengeGUI.GuiWindow(world, x, y, z, player);
			return null;
		}
	}

	public List<MetricProblem> makeProblem(String metric) {
		List<MetricProblem> p = new ArrayList<>();
		problems.put(metric, p);
		return p;
	}

	public void closeAllEditorsExcept(WindowAdapter windowAdapter) {
		while(!openEditors.isEmpty()) {
			if(openEditors.get(0).isVisible() && (windowAdapter==null || !openEditors.get(0).equals(windowAdapter))) {
				openEditors.get(0).setVisible(false);
				openEditors.get(0).dispose();
			}
			openEditors.remove(0);
		}
	}

	public ProblemScore createMetricScore(String metricName) {
		ScoreObjective scoreBoard = arena.getScoreBoard();
		final String scoreName = turnIntoScoreName(metricName);
		final ProblemScore e = new ProblemScore(scoreName, scoreBoard.getScoreboard().getOrCreateScore(scoreName, scoreBoard));
		problemScores.add(e);
		return e;
	}

	public String turnIntoScoreName(String metricName) {
		return metricName.substring(0, 1).toUpperCase() + metricName.substring(1) +" problem";
	}
}
