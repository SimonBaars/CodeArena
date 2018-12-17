package nl.sandersimon.clonedetection.thread;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.challenge.CodeArena;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.common.TestingCommons;
import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.model.Location;

public class CloneDetectionThread extends Thread {
	
	private static CloneDetectionThread worker;
	private final String project;
	private final String type;
	private final String similarityPercentage;

	public CloneDetectionThread(String project) {
		this(project, true);
	}
	
	public CloneDetectionThread(String project, boolean start) {
		this.project = project;
		if(start) start();
		this.type = "";
		this.similarityPercentage = "";
	}

	public CloneDetectionThread(String project, String type, String similarityPercentage) {
		this.project = project;
		this.type = type;
		if(similarityPercentage.length()>0 && !similarityPercentage.contains("."))
			this.similarityPercentage = similarityPercentage+".0";
		else this.similarityPercentage = similarityPercentage;
		start();
	}

	public void run() {
		CloneDetection.get().executeTill("calculateCodeDuplication(|file://"+SavePaths.getProjectFolder()+project+"/|"+addIfNotEmpty(type)+addIfNotEmpty(similarityPercentage)+")", '\n');
		populateResult();
		CloneDetection.get().writeAllMetricsToFile();
		CloneDetection.get().waitUntilExecuted();
		Minecraft.getMinecraft().player.sendChatMessage("All clones have been successfully parsed!");
	}
	
	private String addIfNotEmpty(String string) {
		return string.isEmpty() ? "" : ", "+string;
	}

	public void populateResult(){
		CloneDetection c = CloneDetection.get();
		List<CloneClass> locs = c.getClones();
		while(true) {
			String unitSizeString = c.waitUntilExecuted('\n').get(0);
			int unitSize = Integer.parseInt(unitSizeString);
			if(unitSize == 0)
				break;
			c.getTotalAmountOfLinesInProject().increaseScore(unitSize);
			c.calculateClonePercentage();
			while(true) {
				String bufferSizeString = c.waitUntilExecuted('\n').get(0);
				int bufferSize = Integer.parseInt(bufferSizeString);
				if(bufferSize == 0)
					break;
				
				String dupLinesString = c.waitUntilExecuted('\n').get(0);
				int dupLines = Integer.parseInt(dupLinesString);
				c.getTotalAmountOfClonedLinesInProject().increaseScore(dupLines);
				
				
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
	
	public static void startWorker(MinecraftServer server, ICommandSender s, String[] args) {
		//System.out.println("Spawn at pos "+s.getPosition());
		//new StructureCreatorClient("arena", s.getPosition().getX()+95, s.getPosition().getY()-2, s.getPosition().getZ()+80	, false, 0);
		CloneDetection.get().setArena(new CodeArena(s.getPosition().getX(), s.getPosition().getY(), s.getPosition().getZ()));
		CloneDetection.get().initScoreboards();
		if(worker!=null && worker.isAlive()) {
			s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.RED, "Sorry, but I'm still busy detecting clones! Please wait a little longer."));
			return;
		}
		s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.DARK_GREEN, "Searching for clones, please wait..."));
		
		worker = new CloneDetectionThread(args[0], args.length > 1 ? args[1] : "", args.length > 2 ? args[2] : "");
	}

	public static CloneDetectionThread getWorker() {
		return worker;
	}
}
