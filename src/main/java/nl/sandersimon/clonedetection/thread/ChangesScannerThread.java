package nl.sandersimon.clonedetection.thread;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.common.TestingCommons;
import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.model.CloneMetrics;
import nl.sandersimon.clonedetection.model.CloneScore;
import nl.sandersimon.clonedetection.model.Location;

public class ChangesScannerThread extends Thread {
	
	private static ChangesScannerThread worker;
	CloneClass c;
	private boolean before;
	private CloneMetrics metrics = new CloneMetrics();
	private final ICommandSender mySender;

	public ChangesScannerThread(ICommandSender s, CloneClass c, boolean before) {
		this.c = c;
		this.before = before;
		this.mySender = s;
	}

	public void run() {
		CloneDetection cloneDetection = CloneDetection.get();
		if(!before) {
			while(cloneDetection.before == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		cloneDetection.executeRascal(cloneDetection.getScanIn(), cloneDetection.getScanOut(), "doPartialScan("+c.rascalLocList()+")", '\n');
		populateResult();
		cloneDetection.waitUntilExecuted(cloneDetection.getScanIn(), cloneDetection.getRascalReadyState());
		if(before) {
			cloneDetection.before = metrics;
		} else {
			mySender.sendMessage(Commons.format(TextFormatting.AQUA, "Clone fix results (counted only over the edited files:"));
			for(CloneScore score : metrics.getScores()) {
				int points = score.getScorePoints();
				int prevPoints = cloneDetection.before.getScoreByName(score.getName()).getScorePoints();
				int scoreGain = prevPoints - points;
				if(points < prevPoints) {
					cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(TextFormatting.DARK_GREEN, "Well done! "+score.getName()+ " went from "+prevPoints+" to "+points+"! Because of this you gain "+scoreGain+" points!")));
				} else if(points == prevPoints) {
					cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(TextFormatting.WHITE, "Your fix didn't change anything for "+score.getName()+ ".")));
				} else {
					cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(TextFormatting.RED, "Too bad! "+score.getName()+ " went from "+prevPoints+" to "+points+"! Because of this you lose "+Math.abs(scoreGain)+" points!")));
				}
				cloneDetection.eventHandler.nextTickActions.add(() -> cloneDetection.getArena().increaseScore(scoreGain));
			}
			
			cloneDetection.before = null;
		}
	}
	
	public void populateResult(){
		CloneDetection c = CloneDetection.get();
		List<CloneClass> locs = c.getClones();
		while(true) {
			String unitSizeString = c.waitUntilExecuted('\n').get(0);
			int unitSize = Integer.parseInt(unitSizeString);
			if(unitSize == 0)
				break;
			metrics.getTotalAmountOfLinesInProject().increaseScore(unitSize);
			metrics.calculateClonePercentage();
			while(true) {
				String bufferSizeString = c.waitUntilExecuted('\n').get(0);
				int bufferSize = Integer.parseInt(bufferSizeString);
				if(bufferSize == 0)
					break;
				
				String dupLinesString = c.waitUntilExecuted('\n').get(0);
				int dupLines = Integer.parseInt(dupLinesString);
				metrics.getTotalAmountOfClonedLinesInProject().increaseScore(dupLines);
				
				
				String res = c.readBuffer(bufferSize);
				
				//System.out.println(res+", "+bufferSizeString);
				c.waitUntilExecuted('\n');
				
				int listLoc = 1;
				while (listLoc < res.length() && res.charAt(listLoc) == '<') {
					CloneClass loc = new CloneClass(metrics);
					listLoc = parseList(loc, res, listLoc+1)+2;
					locs.add(loc);
					c.eventHandler.nextTickActions.add(() -> c.getArena().create(loc, 1));
					try {
						TestingCommons.writeStringToFile(new File(SavePaths.createDirectoryIfNotExists(SavePaths.getSaveFolder())+"clone-"+c.hashCode()+".txt"), c.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private int parseList(CloneClass loc, String res, int elementLoc) {
		elementLoc = Location.parseNumber(res, CloneClass::setLines, loc, elementLoc)+1;
		while(res.charAt(elementLoc) == '|') {
			int indexOf = res.indexOf(')', elementLoc+1);
			if(indexOf == -1)
				break; // Not a valid location
			String stringRep = res.substring(elementLoc+1, indexOf);
			loc.add(metrics, Location.construct(stringRep));
			elementLoc += stringRep.length()+3;
		}
		return elementLoc;
	}
	
	public static void startWorker(ICommandSender s, CloneClass c, boolean before) {
		worker = new ChangesScannerThread(s, c, before);
		
		/*while(worker.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		if(before)*/
	}

	public static ChangesScannerThread getWorker() {
		return worker;
	}
}
