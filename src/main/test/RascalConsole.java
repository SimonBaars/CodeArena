import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import nl.sandersimon.clonedetection.CloneDetection;
import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.model.MetricProblem;
import nl.sandersimon.clonedetection.model.Location;

public class RascalConsole {
	@Test
	public void startConsole() {
		//CloneDetection d = new CloneDetection();
		//d.preInit(null);
		//d.init(null);
		printExampleCommand();
		/*Scanner scanner = new Scanner(System.in);
		while(true) {
			List<String> output = d.executeRascal(scanner.nextLine());
			for(String o : output) {
				System.out.println(o);
			}
		}*/
	}

	private void printExampleCommand() {
		MetricProblem foundLocs = new MetricProblem();
		//String project = "ProjectWithDuplicateBetweenFiles";
		String project = "demoProject";
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
		
		System.out.println("calculateCodeDuplication("+foundLocs.rascalLocList()+")");
	}
}
