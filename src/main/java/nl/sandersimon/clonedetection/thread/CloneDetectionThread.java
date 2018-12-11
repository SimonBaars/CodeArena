package nl.sandersimon.clonedetection.thread;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.model.Location;

public class CloneDetectionThread extends Thread {
	
	private static CloneDetectionThread worker;
	private final String project;

	public CloneDetectionThread(String project) {
		this(project, true);
	}
	
	public CloneDetectionThread(String project, boolean start) {
		this.project = project;
		if(start) start();
	}

	public void run() {
		CloneDetection.get().executeTill("calculateCodeDuplication(|file://"+SavePaths.getProjectFolder()+project+"/|)", '\n');
		String bufferSizeString = CloneDetection.get().waitUntilExecuted('\n').get(0);
		int bufferSize = Integer.parseInt(bufferSizeString);
		String res = CloneDetection.get().readBuffer(bufferSize);
		//System.out.println(res+", "+bufferSizeString);
		CloneDetection.get().waitUntilExecuted();
		CloneDetection.get().setClones(populateResult(res));
	}
	
	public List<CloneClass> populateResult(String res){
		List<CloneClass> locs = new ArrayList<>();
		int listLoc = 1;
		while (listLoc < res.length() && res.charAt(listLoc) == '<') {
			CloneClass loc = new CloneClass();
			listLoc = parseList(loc, res, listLoc+1)+2;
			locs.add(loc);
		}
		return locs;
	}

	private int parseList(CloneClass loc, String res, int elementLoc) {
		elementLoc = Location.parseNumber(res, CloneClass::setLines, loc, elementLoc)+1;
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
	
	public static void startWorker(ICommandSender s, String project) {
		if(worker!=null && worker.isAlive()) {
			s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.RED, "Sorry, but I'm still busy detecting clones! Please wait a little longer."));
			return;
		}
		s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.DARK_GREEN, "Searching for clones, please wait..."));
		worker = new CloneDetectionThread(project);
	}

	public static CloneDetectionThread getWorker() {
		return worker;
	}
}
