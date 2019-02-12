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
import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.model.Location;

public class RascalConsole {
	@Test
	public void startConsole() throws IOException, InterruptedException {
		CloneDetection d = new CloneDetection();
		d.preInit(null);
		d.init(null);
		printExampleCommand();
		Scanner scanner = new Scanner(System.in);
		while(true) {
			try {
				d.getRascalOut().write(scanner.nextLine());
				d.getRascalOut().newLine();
				d.getRascalOut().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while(d.getRascalIn().ready()) {
				int read = d.getRascalIn().read();
				System.out.print((char)read+"");
				Thread.sleep(10);
			}
		}
	}

	private void printExampleCommand() {
		CloneClass foundLocs = new CloneClass();
		String project = "ProjectWithDuplicateBetweenFiles";
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

		return "calculateCodeDuplication("+foundLocs.rascalLocList()+")";
	}
}
