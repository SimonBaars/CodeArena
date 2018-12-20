package nl.sandersimon.clonedetection.thread;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.challenge.CodeArena;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.model.CloneMetrics;
import nl.sandersimon.clonedetection.model.CloneScore;
import nl.sandersimon.clonedetection.model.Location;

public class ChangesScannerThread extends Thread {
	
	private static ChangesScannerThread worker;
	private CloneClass c;
	private boolean before;
	private CloneMetrics metrics = new CloneMetrics();
	private final ICommandSender mySender;
	private final String type;
	private final String similarityPercentage;

	public ChangesScannerThread(ICommandSender s, String type, String similarityPercentage, CloneClass c, boolean before) {
		this.c = c;
		this.before = before;
		this.mySender = s;
		if(similarityPercentage.length()>0 && !similarityPercentage.contains("."))
			this.similarityPercentage = similarityPercentage+".0";
		else this.similarityPercentage = similarityPercentage;
		this.type = type;
		start();
	}
	
	private String addIfNotEmpty(String string) {
		return string.isEmpty() ? "" : ", "+string;
	}

	public void run() {
		//System.out.println("Running changes scan");
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
		//System.out.println("Gonna exx rascal");
		cloneDetection.executeRascal(cloneDetection.getScanIn(), cloneDetection.getScanOut(), "calculateCodeDuplication("+c.rascalLocList()+addIfNotEmpty(type)+addIfNotEmpty(similarityPercentage)+")", '\n');
		//System.out.println("populate");
		populateResult();
		//System.out.println("Waiting till finished...");
		cloneDetection.waitUntilExecuted(cloneDetection.getScanIn(), cloneDetection.getRascalReadyState());
		//System.out.println("Cleaning up...");
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
				cloneDetection.getArena().increaseScore(scoreGain);
				if(!score.getName().startsWith("Biggest")) cloneDetection.getMetrics().getScoreByName(score.getName()).increaseScore(-scoreGain);
			}
			cloneDetection.calculateClonePercentage();
			if(metrics.getTotalNumberOfCloneClasses().getScorePoints() < cloneDetection.before.getTotalNumberOfCloneClasses().getScorePoints())
				cloneDetection.eventHandler.nextTickActions.add(() -> cloneDetection.getArena().killSpider(c));
			cloneDetection.before = null;
			if(cloneDetection.getMetrics().getTotalNumberOfCloneClasses().getScorePoints() == 0) {
				cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.DARK_GREEN, "Your project contains no clones congrats!!")));
				cloneDetection.eventHandler.nextTickActions.add(() -> cloneDetection.getArena().endChallengeForAllPlayers());
			}
		}
	}
	
	public void populateResult(){
		CloneDetection c = CloneDetection.get();
		List<CloneClass> locs = c.getClones();
		while(true) {
			//System.out.println("UNIT SIZE");
			String unitSizeString = c.waitUntilExecuted(c.getScanIn(), '\n').get(0);
			//System.out.println("UNIT SIZE = "+unitSizeString);
			int unitSize = Integer.parseInt(unitSizeString);
			if(unitSize == 0)
				break;
			metrics.getTotalAmountOfLinesInProject().increaseScore(unitSize);
			metrics.calculateClonePercentage();
			while(true) {
				//System.out.println("BUFFER SIZE");
				String bufferSizeString = c.waitUntilExecuted(c.getScanIn(), '\n').get(0);
				//System.out.println("BUFFER SIZE = "+bufferSizeString);
				int bufferSize = Integer.parseInt(bufferSizeString);
				if(bufferSize == 0)
					break;
				
				//System.out.println("DUPLINES SIZE");
				String dupLinesString = c.waitUntilExecuted(c.getScanIn(), '\n').get(0);
				//System.out.println("DUPLINES SIZE = "+dupLinesString);
				int dupLines = Integer.parseInt(dupLinesString);
				metrics.getTotalAmountOfClonedLinesInProject().increaseScore(dupLines);
				
				//System.out.println("READBUFFER SIZE");
				String res = c.readBuffer(c.getScanIn(), bufferSize);
				//System.out.println("READBUFFER SIZE = "+bufferSize);
				
				//System.out.println(res+", "+bufferSizeString);
				c.waitUntilExecuted(c.getScanIn(), '\n');
				
				int listLoc = 1;
				while (listLoc < res.length() && res.charAt(listLoc) == '<') {
					CloneClass loc = new CloneClass(metrics);
					listLoc = parseList(loc, res, listLoc+1)+2;
					locs.add(loc);
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
		CodeArena arena = CloneDetection.get().getArena();
		worker = new ChangesScannerThread(s, arena.getCloneType(), arena.getSimilarityPerc(), c, before);
		
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
