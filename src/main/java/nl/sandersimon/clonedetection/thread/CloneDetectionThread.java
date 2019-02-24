package nl.sandersimon.clonedetection.thread;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.challenge.CodeArena;
import nl.sandersimon.clonedetection.common.Commons;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.common.TestingCommons;
import nl.sandersimon.clonedetection.model.Location;
import nl.sandersimon.clonedetection.model.MetricProblem;

public class CloneDetectionThread extends Thread {
	
	private static CloneDetectionThread worker;
	private final ICommandSender mySender;
	private final String project;
	public static final String[] NO_METRICS = {"loader.rsc", "metricscommons.rsc"};
	
	public CloneDetectionThread(String project, String type, String similarityPercentage, ICommandSender s, String nLines) {
		this.project = project;
		this.mySender = s;
		start();
	}

	public void run() {
		CloneDetection cloneDetection = CloneDetection.get();
		MetricProblem foundLocs = new MetricProblem();
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
		
		cloneDetection.executeTill("getAsts("+foundLocs.rascalLocList()+");", '>');
		System.out.println("ASTS RETRIEVED");
		
		String[] metrics = new File(SavePaths.getRascalFolder()).list((dir, name) -> Arrays.stream(NO_METRICS).noneMatch(e -> e.equals(name)));
		for(String metric : metrics) {
			String metricName = metric.replace(".rsc", "");
			cloneDetection.executeTill("import "+metricName+";", '>');
			cloneDetection.executeTill("calcMetric("+metricName+");", '\n');
			System.out.println("Metric "+metric+" retrieved");
			populateResult(metric);
			cloneDetection.waitUntilExecuted();
		}
		cloneDetection.eventHandler.nextTickActions.add(() -> mySender.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.DARK_GREEN, "All clones have been successfully parsed!")));
	}
	
	private String addIfNotEmpty(String string) {
		return string.isEmpty() ? "" : ", "+string;
	}

	public void populateResult(String metric){
		CloneDetection c = CloneDetection.get();
		List<MetricProblem> locs = c.getClones();
		
		int bufferSize;
		while((bufferSize = parseNumberFromRascal()) != 0 ) {
			int dupLines = parseNumberFromRascal();


			String res = c.readBuffer(bufferSize);

			//System.out.println(res+", "+bufferSizeString);
			c.waitUntilExecuted('\n');

			int listLoc = 1;
			while (listLoc < res.length() && res.charAt(listLoc) == '<') {
				MetricProblem loc = new MetricProblem();
				listLoc = parseList(loc, res, listLoc+1)+2;
				locs.add(loc);
				c.eventHandler.nextTickActions.add(() -> c.getArena().create(loc));
			}
		}
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
	
	public static void startWorker(MinecraftServer server, ICommandSender s, String[] args) {
		//System.out.println("Spawn at pos "+s.getPosition());
		//new StructureCreatorClient("arena", s.getPosition().getX()+95, s.getPosition().getY()-2, s.getPosition().getZ()+80	, false, 0);
		String cloneType = args.length > 1 ? args[1] : "1";
		String similarityPerc = args.length > 2 ? args[2] : "0.0";
		String nLines = args.length > 3 ? args[3] : "6";
		CloneDetection.get().packages.clear();
		CloneDetection.get().setArena(new CodeArena(s.getPosition().getX(), s.getPosition().getY(), s.getPosition().getZ(),  cloneType, similarityPerc, nLines));
		CloneDetection.get().initScoreboards();
		if(worker!=null && worker.isAlive()) {
			s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.RED, "Sorry, but I'm still busy detecting clones! Please wait a little longer."));
			return;
		}
		s.sendMessage(Commons.format(net.minecraft.util.text.TextFormatting.DARK_GREEN, "Searching for clones, please wait..."));
		
		worker = new CloneDetectionThread(args[0], cloneType, similarityPerc, s, nLines);
	}

	public static CloneDetectionThread getWorker() {
		return worker;
	}
}
