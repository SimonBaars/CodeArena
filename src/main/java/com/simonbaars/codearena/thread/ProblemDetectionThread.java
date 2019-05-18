package com.simonbaars.codearena.thread;

import static com.simonbaars.codearena.thread.ProblemDetectionGoal.DETECTION;
import static com.simonbaars.codearena.thread.ProblemDetectionGoal.SCANAFTER;
import static com.simonbaars.codearena.thread.ProblemDetectionGoal.SCANBEFORE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.SequenceObservable;
import com.simonbaars.clonerefactor.SequenceObserver;
import com.simonbaars.clonerefactor.ast.CloneParser;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.challenge.CodeArena;
import com.simonbaars.codearena.common.Commons;
import com.simonbaars.codearena.common.SavePaths;
import com.simonbaars.codearena.model.MetricProblem;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;

public class ProblemDetectionThread extends Thread {
	
	private static ProblemDetectionThread worker;
	private final ICommandSender mySender;
	private final String project;
	private ProblemDetectionGoal goal;
	private MetricProblem scanProblem;
	private static final List<Integer> problemSizes = new ArrayList<>();
	private static final MetricProblem foundLocs = new MetricProblem();
	private SequenceObserver observer;
	
	public ProblemDetectionThread(ProblemDetectionGoal g, ICommandSender s, MetricProblem p, String project) {
		this.project = project;
		this.mySender = s;
		this.goal = g;
		this.scanProblem = p;
		start();
	}

	public void run() {
		CloneDetection cloneDetection = CloneDetection.get();
		if(goal == DETECTION) {
			observer = (ProblemType problem, Sequence sequence, int problemSize) -> {
				MetricProblem loc = new MetricProblem(problem, problemSize, sequence);
				cloneDetection.eventHandler.nextTickActions.add(() -> cloneDetection.getArena().create(problem, loc));
				cloneDetection.getScoreForType(problem).incrementScore();
			};
			SequenceObservable.get().subscribe(observer);
			new CloneParser().parse(scanProjectForJavaFiles(SavePaths.getProjectFolder()+project));
			SequenceObservable.get().unsubscribe(observer);
			cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.DARK_GREEN, "All metrics have been successfully parsed!")));
		} else {
			List<Integer> before = new ArrayList<>(problemSizes);
			problemSizes.clear();
			Set<File> files = scanProblem.getLocations().stream().map(e -> e.getFile().toFile()).collect(Collectors.toSet());
			observer = (ProblemType problem, Sequence sequence, int problemSize) -> {
				if(problem == scanProblem.getType()) {
					problemSizes.add(problemSize);
				}
			};
			SequenceObservable.get().subscribe(observer);
			new CloneParser().parse(files);
			if(goal == SCANAFTER) {
				rewardPointsForFix(before, problemSizes);
			}
		}
		worker = null;
	}
	
	private static List<File> scanProjectForJavaFiles(String path) {
		List<File> javaFiles = new ArrayList<>();
		try {
			Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
			    	File file = filePath.toFile();
					if(file.getName().endsWith(".java"))
			    		javaFiles.add(file);
			    	return FileVisitResult.CONTINUE;
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return javaFiles;
	}

	private void rewardPointsForFix(List<Integer> before, List<Integer> after) {
		CloneDetection cloneDetection = CloneDetection.get();
		if(after.size()<before.size()) {
			cloneDetection.getArena().increaseScore(5);
			cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(TextFormatting.DARK_GREEN, "Well done on improving the metric! You are awarded 5 emeralds!")));
			cloneDetection.eventHandler.nextTickActions.add(() -> cloneDetection.getArena().killSpider(scanProblem));
			cloneDetection.getScoreForType(scanProblem.getType()).increaseScore(after.size()-before.size());
		} else if(after.stream().mapToInt(e -> e).sum()<before.stream().mapToInt(e -> e).sum()){
			CloneDetection.get().getArena().increaseScore(1);
			cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(TextFormatting.YELLOW, "Your fix did not fix the entire issue, but did improve upon it. You are awarded 1 emerald!")));
		} else {
			cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(TextFormatting.RED, "The problem was not fixed! No emeralds for you!")));
		}
	}

	public static void startWorker(ICommandSender s, String projectName) {
		if(worker != null) {
			s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.RED, "We are busy... Please wait!"));
			return;
		}
		//System.out.println("Spawn at pos "+s.getPosition());
		//new StructureCreatorClient("arena", s.getPosition().getX()+95, s.getPosition().getY()-2, s.getPosition().getZ()+80	, false, 0);
		CloneDetection.get().packages.clear();
		CloneDetection.get().setArena(new CodeArena(s.getPosition().getX(), s.getPosition().getY(), s.getPosition().getZ()));
		//CloneDetection.get().initScoreboards();
		if(worker!=null && worker.isAlive()) {
			s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.RED, "Sorry, but I'm still busy detecting code problems! Please wait a little longer."));
			return;
		}
		s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.DARK_GREEN, "Searching for code problems, please wait..."));
		
		worker = new ProblemDetectionThread(DETECTION, s, null, projectName);
	}
	
	public static void startWorker(ICommandSender s, MetricProblem p, boolean before) {
		if(worker != null) {
			s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.RED, "We are busy... Please wait!"));
			return;
		}
		worker = new ProblemDetectionThread(before ? SCANBEFORE : SCANAFTER, s, p, null);
	}

	public static ProblemDetectionThread getWorker() {
		return worker;
	}
}
