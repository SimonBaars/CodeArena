package nl.sandersimon.clonedetection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import nl.sandersimon.clonedetection.challenge.CodeArena;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.common.ResourceCommons;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.common.TestingCommons;
import nl.sandersimon.clonedetection.editor.CodeEditor;
import nl.sandersimon.clonedetection.minecraft.CDEventHandler;
import nl.sandersimon.clonedetection.minecraft.proxy.CommonProxy;
import nl.sandersimon.clonedetection.model.CloneClass;

@Mod(modid = CloneDetection.MODID, name = CloneDetection.NAME, version = CloneDetection.VERSION, dependencies = "required-after:forge@[13.19.0.2129,)", useMetadata = true)
public class CloneDetection
{
	public static final String MODID = "clonedetection";
	public static final String NAME = "Clone Detection";
	public static final String VERSION = "1.0";
	
	public static final CDEventHandler eventHandler = new CDEventHandler();

	private Process rascal;
	private BufferedWriter rascalOut;
	private InputStreamReader rascalIn;
	@Mod.Instance
	private static CloneDetection cloneDetection;
	private List<CloneClass> clones = new ArrayList<>();
	
	private Score totalAmountOfClonedLinesInProject;
	private Score totalAmountOfLinesInProject;
	private Score percentageOfProjectCloned;
	private Score totalNumberOfClones;
	private Score totalNumberOfCloneClasses;
	private Score mostLinesCloneClass;
	private Score mostOccurrentClone;
	private Score biggestCloneClass;
	private Score totalCloneVolume;
	
	 @SidedProxy(clientSide = "nl.sandersimon.clonedetection.minecraft.proxy.ClientProxy", serverSide = "nl.sandersimon.clonedetection.minecraft.proxy.ServerProxy")
	    public static CommonProxy proxy;
	
	List<Score> scores = new ArrayList<>();
	
	private CodeArena arena;
	public List<CodeEditor> openEditors = new ArrayList<>();
	
	public static int dialoge;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
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
		proxy.init(event);
		try {
			System.out.println("Starting Rascal..");
			rascal = getProcess("java -jar "+ResourceCommons.getResource("Rascal.jar").getAbsolutePath(), ResourceCommons.getResource("rascal"));
			executeRascal("import loader;");
			System.out.println("Rascal Started!");
		} catch (IOException e) {
			throw new RuntimeException("Rascal could not be started!", e);
		}
		if(eventHandler!=null)	{
			FMLCommonHandler.instance().bus().register(eventHandler);
			MinecraftForge.EVENT_BUS.register(new nl.sandersimon.clonedetection.minecraft.ForgeEventHandler());
		}
	}
	
	@Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
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
		rascalOut = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
		rascalIn = new InputStreamReader(pr.getInputStream());
		waitUntilExecuted();
		return pr;
	}

	public List<String> executeRascal(String statement){
		return executeRascal(statement, getRascalReadyState());
	}
	
	public List<String> executeRascal(String statement, char till) {
		System.out.println("Executing Rascal: "+statement);
		try {
			rascalOut.write(statement);
			rascalOut.newLine();
			rascalOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return waitUntilExecuted(till);
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

	private char getRascalReadyState() {
		return '>';
	}

	public List<String> waitUntilExecuted(char till) {
		List<String> lines = new ArrayList<>();
		outerloop: while(true) {
			StringBuilder buffer = new StringBuilder();
			try {
				while(rascalIn.ready()) {
					int read = rascalIn.read();
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
	
	public String readBuffer(int bufferSize) {
		char[] cbuf = new char[bufferSize];
		try {
			int read = rascalIn.read(cbuf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(cbuf);
	}

	public List<CloneClass> getClones() {
		return clones;
	}

	public void setClones(List<CloneClass> clones) {
		this.clones = clones;
	}

	public Score getTotalAmountOfClonedLinesInProject() {
		return totalAmountOfClonedLinesInProject;
	}

	public void setTotalAmountOfClonedLinesInProject(Score totalAmountOfClonedLinesInProject) {
		this.totalAmountOfClonedLinesInProject = totalAmountOfClonedLinesInProject;
	}

	public Score getPercentageOfProjectCloned() {
		return percentageOfProjectCloned;
	}

	public void setPercentageOfProjectCloned(Score percentageOfProjectCloned) {
		this.percentageOfProjectCloned = percentageOfProjectCloned;
	}

	public Score getTotalNumberOfClones() {
		return totalNumberOfClones;
	}

	public void setTotalNumberOfClones(Score totalNumberOfClones) {
		this.totalNumberOfClones = totalNumberOfClones;
	}

	public Score getTotalNumberOfCloneClasses() {
		return totalNumberOfCloneClasses;
	}

	public void setTotalNumberOfCloneClasses(Score totalNumberOfCloneClasses) {
		this.totalNumberOfCloneClasses = totalNumberOfCloneClasses;
	}

	public Score getMostLinesCloneClass() {
		return mostLinesCloneClass;
	}

	public void setMostLinesCloneClass(Score mostLinesCloneClass) {
		this.mostLinesCloneClass = mostLinesCloneClass;
	}

	public Score getMostOccurrentClone() {
		return mostOccurrentClone;
	}

	public void setMostOccurrentClone(Score mostOccurrentClone) {
		this.mostOccurrentClone = mostOccurrentClone;
	}

	public Score getBiggestCloneClass() {
		return biggestCloneClass;
	}

	public void setBiggestCloneClass(Score biggestCloneClass) {
		this.biggestCloneClass = biggestCloneClass;
	}

	public CodeArena getArena() {
		return arena;
	}

	public void setArena(CodeArena arena) {
		this.arena = arena;
	}

	public Score getTotalCloneVolume() {
		return totalCloneVolume;
	}

	public void setTotalCloneVolume(Score totalCloneVolume) {
		this.totalCloneVolume = totalCloneVolume;
	}

	public Score getTotalAmountOfLinesInProject() {
		return totalAmountOfLinesInProject;
	}

	public void setTotalAmountOfLinesInProject(Score totalAmountOfLinesInProject) {
		this.totalAmountOfLinesInProject = totalAmountOfLinesInProject;
	}

	public void initScoreboards() {
		ScoreObjective scoreBoard = arena.getScoreBoard();
		createScoreBoard(scoreBoard, this::setTotalAmountOfLinesInProject, "Amount of lines in project");
		createScoreBoard(scoreBoard, this::setTotalAmountOfClonedLinesInProject, "Amount of cloned lines");
		createScoreBoard(scoreBoard, this::setPercentageOfProjectCloned, "Percentage of project cloned");
		createScoreBoard(scoreBoard, this::setTotalNumberOfClones, "Amount of clones");
		createScoreBoard(scoreBoard, this::setTotalNumberOfCloneClasses, "Number of clone classes");
		createScoreBoard(scoreBoard, this::setTotalCloneVolume, "Total clone volume");
		createScoreBoard(scoreBoard, this::setMostLinesCloneClass, "Biggest clone class (in lines)");
		createScoreBoard(scoreBoard, this::setMostOccurrentClone, "Most occurring clone class");
		createScoreBoard(scoreBoard, this::setBiggestCloneClass, "Biggest clone class (in volume)");
	}

	private void createScoreBoard(ScoreObjective scoreBoard, Consumer<Score> setter, String text) {
		Score display = scoreBoard.getScoreboard().getOrCreateScore(text, scoreBoard);
		display.setScorePoints(0);
		scores.add(display);
		setter.accept(display);
	}
	
	public void writeAllMetricsToFile() {
		StringBuilder builder = new StringBuilder();
		for(Score score : scores)
			builder.append(score.getPlayerName()+": "+score.getScorePoints()+System.lineSeparator());
		try {
			TestingCommons.writeStringToFile(new File(SavePaths.createDirectoryIfNotExists(SavePaths.getSaveFolder())+"clone_metrics.txt"), builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void calculateClonePercentage() {
		getPercentageOfProjectCloned().setScorePoints(perc(getTotalAmountOfLinesInProject().getScorePoints(), getTotalAmountOfClonedLinesInProject().getScorePoints()));
	}
	
	private int perc(int total, int partOfTotal) {
		return (int) Math.round((((double)partOfTotal / (double)total) * 100.0));
	}

	public void closeAllEditors() {
		while(openEditors.size()>0) {
			if(openEditors.get(0).isVisible()) {
				openEditors.get(0).setVisible(false);
				openEditors.get(0).dispose();
			}
			openEditors.remove(0);
		}
	}
}
