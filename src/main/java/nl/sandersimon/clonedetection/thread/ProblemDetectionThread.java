package nl.sandersimon.clonedetection.thread;

import static nl.sandersimon.clonedetection.thread.ProblemDetectionGoal.DETECTION;
import static nl.sandersimon.clonedetection.thread.ProblemDetectionGoal.SCANBEFORE;
import static nl.sandersimon.clonedetection.thread.ProblemDetectionGoal.SCANAFTER;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import akka.japi.Pair;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.challenge.CodeArena;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.model.Location;
import nl.sandersimon.clonedetection.model.MetricProblem;
import scala.collection.generic.BitOperations.Int;

public class ProblemDetectionThread extends Thread {
	
	private static ProblemDetectionThread worker;
	private final ICommandSender mySender;
	private final String project;
	private ProblemDetectionGoal goal;
	private MetricProblem scanProblem;
	public static final String[] NO_METRICS = {"loader.rsc", "metricscommons.rsc"};
	private static int beforeMetric = 0;
	private static int beforeProblemSize = 0;
	private static final MetricProblem foundLocs = new MetricProblem();
	
	public ProblemDetectionThread(ProblemDetectionGoal g, ICommandSender s, MetricProblem p, String project) {
		this.project = project;
		this.mySender = s;
		this.goal = g;
		this.scanProblem = p;
		if(g == SCANBEFORE)
			beforeProblemSize = 0;
		start();
	}

	public void run() {
		CloneDetection cloneDetection = CloneDetection.get();
		if(goal == DETECTION) {
			retrieveAsts(cloneDetection);
			findAllProblems(cloneDetection);
			cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.DARK_GREEN, "All metrics have been successfully parsed!")));
		} else {
			cloneDetection.executeTill("scanMetric("+scanProblem.getMetric()+", ["+IntStream.range(0, foundLocs.size()).filter(e -> scanProblem.getLocations().stream().anyMatch(l -> l.getFile().equals(foundLocs.get(e).getFile()))).boxed().map(e -> Integer.toString(e)).collect(Collectors.joining(", "))+"]);", '\n');
			System.out.println("Metric "+scanProblem.getMetric()+" retrieved "+scanProblem.getLocations().stream().map(e -> e.getFile()).collect(Collectors.joining())+ " ==> "+foundLocs.getLocations().stream().map(e -> e.getFile()).collect(Collectors.joining()));
			Pair<Integer, Integer> amount = populateResult(scanProblem.getMetric());
			int amountOfProblemsFound = amount.first();
			int problemSize = amount.second();
			if(goal == SCANBEFORE) {
				beforeMetric = amountOfProblemsFound;
				beforeProblemSize = problemSize;
				System.out.println("Set beforeMetric = "+beforeMetric+", beforeProblemSize = "+problemSize);
			} else {
				rewardPointsForFix(cloneDetection, amountOfProblemsFound, problemSize);
			}
		}
		worker = null;
	}

	private void rewardPointsForFix(CloneDetection cloneDetection, int amountOfProblemsFound, int problemSize) {
		System.out.println("Is beforeMetric = "+beforeMetric+", beforeProblemSize = "+problemSize+" and amountOfProblemsFound = "+amountOfProblemsFound+", problemSize = "+problemSize);
		if(amountOfProblemsFound<beforeMetric) {
			CloneDetection.get().getArena().increaseScore(5);
			cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(TextFormatting.DARK_GREEN, "Well done on improving the metric! You are awarded 5 emeralds!")));
			cloneDetection.eventHandler.nextTickActions.add(() -> cloneDetection.getArena().killSpider(scanProblem));
		} else if(problemSize<beforeProblemSize){
			CloneDetection.get().getArena().increaseScore(1);
			cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(TextFormatting.YELLOW, "Your fix did not fix the entire issue, but did improve upon it. You are awarded 1 emerald!")));
		} else {
			cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(TextFormatting.RED, "The problem was not fixed! No emeralds for you!")));
		}
	}

	private void findAllProblems(CloneDetection cloneDetection) {
		String[] metrics = new File(SavePaths.getRascalFolder()).list((dir, name) -> Arrays.stream(NO_METRICS).noneMatch(e -> e.equals(name)));
		for(String metric : metrics) {
			String metricName = metric.replace(".rsc", "");
			cloneDetection.executeTill("calcMetric("+metricName+");", '\n');
			System.out.println("Metric "+metricName+" retrieved");
			populateResult(metricName);
			cloneDetection.waitUntilExecuted();
		}
	}

	private void retrieveAsts(CloneDetection cloneDetection) {
		foundLocs.getLocations().clear();
		try {
			Files.walkFileTree(Paths.get(SavePaths.getProjectFolder()+project+"/src/"), new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
			    	File file = filePath.toFile();
					if(file.getName().endsWith(".java"))
			    		foundLocs.getLocations().add(new Location(file.getAbsolutePath()));
			    	return FileVisitResult.CONTINUE;
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		cloneDetection.getProblems().clear();
		cloneDetection.executeTill("getAsts("+foundLocs.rascalLocList()+");", '>');
		System.out.println("ASTS RETRIEVED");
	}
	
	private String addIfNotEmpty(String string) {
		return string.isEmpty() ? "" : ", "+string;
	}

	public Pair<Integer, Integer> populateResult(String metric){
		int amountOfProblemsFound = 0;
		int problemSize = 0;
		CloneDetection c = CloneDetection.get();
		List<MetricProblem> locs = goal == DETECTION ? c.makeProblem(metric) : new ArrayList<>();
		
		int bufferSize;
		while((bufferSize = parseNumberFromRascal()) != 0 ) {
			int nLines = parseNumberFromRascal(); //Currently unused, might remove later

			String res = c.readBuffer(bufferSize);
			c.waitUntilExecuted('\n');
			int listLoc = 1;
			while (listLoc < res.length() && res.charAt(listLoc) == '<') {
				MetricProblem loc = new MetricProblem(metric, nLines);
				listLoc = parseList(loc, res, listLoc+1)+2;
				locs.add(loc);
				if(goal == DETECTION)
					c.eventHandler.nextTickActions.add(() -> c.getArena().create(metric, loc));
				amountOfProblemsFound++;
				problemSize+=loc.getLines();
			}
		}
		return new Pair<>(amountOfProblemsFound, problemSize);
	}

	private int parseNumberFromRascal() {
		String bufferSizeString = CloneDetection.get().waitUntilExecuted('\n').get(0);
		return Integer.parseInt(bufferSizeString);
	}

	private int parseList(MetricProblem loc, String res, int elementLoc) {
		elementLoc = Location.parseNumber(res, MetricProblem::setLines, loc, elementLoc)+1;
		while(res.charAt(elementLoc) == '|') {
			int indexOf = res.indexOf(')', elementLoc+1);
			if(indexOf == -1)
				break; // Not a valid location
			String stringRep = res.substring(elementLoc+1, indexOf);
			loc.add(Location.construct(stringRep));
			elementLoc += stringRep.length()+3;
		}
		return elementLoc;
	}
	
	public static void startWorker(ICommandSender s, String projectName) {
		if(worker != null) {
			s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.RED, "Rascal is busy... Please wait!"));
			return;
		}
		//System.out.println("Spawn at pos "+s.getPosition());
		//new StructureCreatorClient("arena", s.getPosition().getX()+95, s.getPosition().getY()-2, s.getPosition().getZ()+80	, false, 0);
		CloneDetection.get().packages.clear();
		CloneDetection.get().setArena(new CodeArena(s.getPosition().getX(), s.getPosition().getY(), s.getPosition().getZ()));
		CloneDetection.get().initScoreboards();
		if(worker!=null && worker.isAlive()) {
			s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.RED, "Sorry, but I'm still busy detecting clones! Please wait a little longer."));
			return;
		}
		s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.DARK_GREEN, "Searching for clones, please wait..."));
		
		worker = new ProblemDetectionThread(DETECTION, s, null, projectName);
	}
	
	public static void startWorker(ICommandSender s, MetricProblem p, boolean before) {
		if(worker != null) {
			s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.RED, "Rascal is busy... Please wait!"));
			return;
		}
		worker = new ProblemDetectionThread(before ? SCANBEFORE : SCANAFTER, s, p, null);
	}

	public static ProblemDetectionThread getWorker() {
		return worker;
	}
}
