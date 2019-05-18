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

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.SequenceObservable;
import com.simonbaars.clonerefactor.SequenceObserver;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.challenge.CodeArena;
import com.simonbaars.codearena.common.Commons;
import com.simonbaars.codearena.common.SavePaths;
import com.simonbaars.codearena.model.Location;
import com.simonbaars.codearena.model.MetricProblem;
import com.simonbaars.codearena.model.ProblemScore;

import akka.japi.Pair;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;

public class ProblemDetectionThread extends Thread {
	
	private static ProblemDetectionThread worker;
	private final ICommandSender mySender;
	private final String project;
	private ProblemDetectionGoal goal;
	private MetricProblem scanProblem;
	private static int beforeProblemSize = 0;
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
			observer = new SequenceObserver() {
				
				@Override
				public void update(ProblemType problem, Sequence sequence, int problemSize) {
					MetricProblem loc = new MetricProblem(problem, problemSize, sequence);
					CloneDetection.get().eventHandler.nextTickActions.add(() -> CloneDetection.get().getArena().create(problem, loc));
					CloneDetection.get().getScoreForType(problem).incrementScore();
				}
			};
			SequenceObservable.get().subscribe(observer);
			Main.cloneDetection(SavePaths.getProjectFolder()+project);
		} else {
			Pair<Integer, Integer> amount = populateResult(scanProblem.getMetric(), null);
			int amountOfProblemsFound = amount.first();
			int problemSize = amount.second();
			if(goal == SCANBEFORE) {
				beforeMetric = amountOfProblemsFound;
				beforeProblemSize = problemSize;
				//System.out.println("Set beforeMetric = "+beforeMetric+", beforeProblemSize = "+problemSize);
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
			for(ProblemScore s : cloneDetection.getProblemScores()) {
				if(s.getName().equals(cloneDetection.turnIntoScoreName(scanProblem.getMetric()))) {
					s.increaseScore(-1);
					break;
				}
			}
		} else if(problemSize<beforeProblemSize){
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
