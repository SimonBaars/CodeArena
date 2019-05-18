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
			
			cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.DARK_GREEN, "All metrics have been successfully parsed!")));
		} else {
			cloneDetection.executeTill("calcMetric("+scanProblem.getMetric()+", "+scanProblem.rascalLocList()+", true);", '\n');
			//System.out.println("Metric "+scanProblem.getMetric()+" retrieved "+scanProblem.getLocations().stream().map(e -> e.getFile()).collect(Collectors.joining())+ " ==> "+foundLocs.getLocations().stream().map(e -> e.getFile()).collect(Collectors.joining()));
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
