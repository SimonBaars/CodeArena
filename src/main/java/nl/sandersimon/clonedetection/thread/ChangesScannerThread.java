package nl.sandersimon.clonedetection.thread;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.challenge.CodeArena;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.common.TestingCommons;
import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.model.CloneMetrics;
import nl.sandersimon.clonedetection.model.Location;

public class ChangesScannerThread extends Thread {
	
	private static ChangesScannerThread worker;
	CloneClass c;
	private boolean before;
	private CloneMetrics metrics = new CloneMetrics();

	public ChangesScannerThread(CloneClass c, boolean before) {
		this.c = c;
		this.before = before;
	}

	public void run() {
		CloneDetection cloneDetection = CloneDetection.get();
		cloneDetection.executeRascal(cloneDetection.getScanIn(), cloneDetection.getScanOut(), "doPartialScan("+c.rascalLocList()+")", '\n');
		populateResult();
		cloneDetection.waitUntilExecuted(cloneDetection.getScanIn(), cloneDetection.getRascalReadyState());
		Minecraft.getMinecraft().player.sendChatMessage("All clones have been successfully parsed!");
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
					CloneClass loc = new CloneClass();
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
			loc.add(Location.construct(stringRep));
			elementLoc += stringRep.length()+3;
		}
		return elementLoc;
	}
	
	public static void startWorker(MinecraftServer server, ICommandSender s, CloneClass c, boolean before) {
		worker = new ChangesScannerThread(c, before);
	}

	public static ChangesScannerThread getWorker() {
		return worker;
	}
}
