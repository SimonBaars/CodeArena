package nl.sandersimon.clonedetection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
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
import nl.sandersimon.clonedetection.challenge.CodeArena;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.common.ResourceCommons;
import nl.sandersimon.clonedetection.editor.CodeEditor;
import nl.sandersimon.clonedetection.minecraft.CDEventHandler;
import nl.sandersimon.clonedetection.minecraft.gui.CloneMenuKey;
import nl.sandersimon.clonedetection.minecraft.gui.EndChallengeGUI;
import nl.sandersimon.clonedetection.minecraft.gui.GUISetupCloneFinding;
import nl.sandersimon.clonedetection.minecraft.proxy.CommonProxy;
import nl.sandersimon.clonedetection.model.MetricProblem;
import nl.sandersimon.clonedetection.model.ProblemScore;

@Mod(modid = CloneDetection.MODID, name = CloneDetection.NAME, version = CloneDetection.VERSION, dependencies = "required-after:forge@[13.19.0.2129,)", useMetadata = true)
public class CloneDetection
{
	public static final String MODID = "clonedetection";
	public static final String NAME = "Clone Detection";
	public static final String VERSION = "1.0";
	
	public static final CDEventHandler eventHandler = new CDEventHandler();

	private Process rascal;
	private Process partialScan;
	private BufferedWriter rascalOut = null;
	private InputStreamReader rascalIn = null;
	private BufferedWriter scanOut = null;
	private InputStreamReader scanIn = null;
	@Mod.Instance
	private static CloneDetection cloneDetection;
	private final Map<String, List<MetricProblem>> problems = new HashMap<>();
	
	public Map<String, List<MetricProblem>> getProblems() {
		return problems;
	}

	private final List<ProblemScore> problemScores = new ArrayList<>();
	
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
			MinecraftForge.EVENT_BUS.register(new nl.sandersimon.clonedetection.minecraft.ForgeEventHandler());
			NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
			new CloneMenuKey().registerRenderers();
		}
		try {
			System.out.println("Starting Rascal..");
			rascal = getProcess("java -jar "+ResourceCommons.getResource("Rascal.jar").getAbsolutePath(), ResourceCommons.getResource("rascal"));
			partialScan = getProcess("java -jar "+ResourceCommons.getResource("Rascal.jar").getAbsolutePath(), ResourceCommons.getResource("rascal"));
			waitUntilExecuted(rascalIn, getRascalReadyState());
			executeRascal("import loader;");
			waitUntilExecuted(scanIn, getRascalReadyState());
			executeRascal(scanIn, scanOut, "import loader;", getRascalReadyState());
			System.out.println("Rascal Started!");
		} catch (IOException e) {
			throw new RuntimeException("Rascal could not be started!", e);
		}
	}
	
	@Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        if(proxy != null)	
        	proxy.postInit(e);
    }

	private Process getProcess(String command, File dir) throws IOException {
		String[] com;
		if(Commons.getOS().isUnix())
			com = new String[]{"bash", "-c", command};
		else 
			com = command.split(" ");
		ProcessBuilder prb = new ProcessBuilder(com);
		prb.directory(dir);
		Process pr = prb.start();
		if(rascalOut == null) rascalOut = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
		else scanOut = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
		if(rascalIn == null) rascalIn = new InputStreamReader(pr.getInputStream());
		else scanIn = new InputStreamReader(pr.getInputStream());
		return pr;
	}

	public List<String> executeRascal(String statement){
		return executeRascal(statement, getRascalReadyState());
	}
	
	public List<String> executeRascal(String statement, char till) {
		return executeRascal(rascalIn, rascalOut, statement, till);
	}
	
	public List<String> executeRascal(InputStreamReader r, BufferedWriter w, String statement, char till) {
		System.out.println("Executing Rascal: "+statement);
		try {
			w.write(statement);
			w.newLine();
			w.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readBuffer(r, statement.length());
		return waitUntilExecuted(r, till);
	}

	public String executeSingleLineRascal(String statement) {
		return executeRascal(statement).get(1);
	}
	
	public String executeTill(String statement, char till) {
		return executeRascal(statement, till).get(0);
	}
	
	public List<String> waitUntilExecuted() {
		return waitUntilExecuted(getRascalReadyState());
	}

	public char getRascalReadyState() {
		return '>';
	}
	
	public List<String> waitUntilExecuted(char till) {
		return waitUntilExecuted(rascalIn, till);
	}

	public List<String> waitUntilExecuted(InputStreamReader i, char till) {
		List<String> lines = new ArrayList<>();
		outerloop: while(true) {
			StringBuilder buffer = new StringBuilder();
			try {
				while(i.ready()) {
					int read = i.read();
					System.out.print((char)read+"");
					if((char)read == till) {
						lines.add(buffer.toString());
						break outerloop;
					}
					if(read == '\n') {
						lines.add(buffer.toString());
						buffer.setLength(0);
					} else buffer.append((char)read);
				}
				Thread.sleep(400);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		return lines;
	}
	
	public String readBuffer(InputStreamReader i, int bufferSize) {
		char[] cbuf = new char[bufferSize];
		try {
			int read = i.read(cbuf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(cbuf);
		return new String(cbuf);
	}
	
	public String readBuffer(int bufferSize) {
		return readBuffer(rascalIn, bufferSize);
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
		while(!openEditors.isEmpty()) {
			if(openEditors.get(0).isVisible()) {
				openEditors.get(0).setVisible(false);
				openEditors.get(0).dispose();
			}
			openEditors.remove(0);
		}
	}

	public BufferedWriter getRascalOut() {
		return rascalOut;
	}

	public InputStreamReader getRascalIn() {
		return rascalIn;
	}

	public BufferedWriter getScanOut() {
		return scanOut;
	}

	public InputStreamReader getScanIn() {
		return scanIn;
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
}
