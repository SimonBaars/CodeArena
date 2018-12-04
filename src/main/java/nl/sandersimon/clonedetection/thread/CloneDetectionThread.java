package nl.sandersimon.clonedetection.thread;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.model.Location;

public class CloneDetectionThread extends Thread {
	
	private static CloneDetectionThread worker;
	private final String project;

	public CloneDetectionThread(String project) {
		this.project = project;
		start();
	}

	public void run() {
		CloneDetection.get().executeTill("calculateCodeDuplication(|file://"+CloneDetection.PROJECT_FOLDER+project+"/|)", '\n');
		String bufferSizeString = CloneDetection.get().waitUntilExecuted('\n').get(0);
		int bufferSize = Integer.parseInt(bufferSizeString);
		String res = CloneDetection.get().readBuffer(bufferSize);
		CloneDetection.get().waitUntilExecuted();
		System.out.println("Amount of locs = "+ populateResult(res).get(0).size());
		System.out.println("DONE!");
	}
	
	public List<List<Location>> populateResult(String res){
		List<List<Location>> locs = new ArrayList<>();
		int listLoc = 1;
		while (listLoc < res.length() && res.charAt(listLoc) == '[') {
			List<Location> loc = new ArrayList<>();
			listLoc = parseList(loc, res, listLoc+1)+1;
			locs.add(loc);
		}
		return locs;
	}

	private int parseList(List<Location> loc, String res, int elementLoc) {
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
}
