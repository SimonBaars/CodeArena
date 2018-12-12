package nl.sandersimon.clonedetection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import nl.sandersimon.clonedetection.challenge.CodeArena;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.common.ResourceCommons;
import nl.sandersimon.clonedetection.model.CloneClass;

@Mod(modid = CloneDetection.MODID, name = CloneDetection.NAME, version = CloneDetection.VERSION)
public class CloneDetection
{
	public static final String MODID = "clonedetection";
	public static final String NAME = "Clone Detection";
	public static final String VERSION = "1.0";
	
	public static final nl.sandersimon.clonedetection.minecraft.EventHandler eventHandler = new nl.sandersimon.clonedetection.minecraft.EventHandler();

	private Process rascal;
	private BufferedWriter rascalOut;
	private InputStreamReader rascalIn;
	private static CloneDetection cloneDetection;
	private List<CloneClass> clones;
	
	private Score totalAmountOfClonedLinesInProject;
	private Score percentageOfProjectCloned;
	private Score totalNumberOfClones;
	private Score totalNumberOfCloneClasses;
	private Score mostLinesCloneClass;
	private Score mostOccurrentClone;
	private Score biggestCloneClass;
	
	private CodeArena arena;
	
	public static int dialoge;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		cloneDetection = this;
		ResourceCommons.extractResources();
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		event.registerServerCommand(new CloneCommand());
	}

	public static CloneDetection get() {
		return cloneDetection;
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
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

	public void initScoreboards() {
		ScoreObjective scoreBoard = arena.getScoreBoard();
		totalAmountOfClonedLinesInProject = scoreBoard.getScoreboard().getOrCreateScore("Amount of cloned lines", scoreBoard);
		totalAmountOfClonedLinesInProject.setScorePoints(0);
		percentageOfProjectCloned = scoreBoard.getScoreboard().getOrCreateScore("Percentage of project cloned", scoreBoard);
		percentageOfProjectCloned.setScorePoints(0);
		totalNumberOfClones = scoreBoard.getScoreboard().getOrCreateScore("Total amount of clones", scoreBoard);
		totalNumberOfClones.setScorePoints(0);
		totalNumberOfCloneClasses = scoreBoard.getScoreboard().getOrCreateScore("Total number of clone classes", scoreBoard);
		totalNumberOfCloneClasses.setScorePoints(0);
		mostLinesCloneClass = scoreBoard.getScoreboard().getOrCreateScore("Biggest clone class (in lines)", scoreBoard);
		mostLinesCloneClass.setScorePoints(0);
		mostOccurrentClone = scoreBoard.getScoreboard().getOrCreateScore("Most occurring clone class", scoreBoard);
		mostOccurrentClone.setScorePoints(0);
		biggestCloneClass = scoreBoard.getScoreboard().getOrCreateScore("Biggest clone class (in volume)", scoreBoard);
		biggestCloneClass.setScorePoints(0);
	}
}
