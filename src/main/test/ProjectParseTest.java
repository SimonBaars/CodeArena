import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.Test;

import nl.sandersimon.clonedetection.common.SavePaths;
import nl.sandersimon.clonedetection.model.CloneClass;
import nl.sandersimon.clonedetection.model.Location;

public class ProjectParseTest {

	@Test
	public void test() {
		CloneClass foundLocs = new CloneClass();
		try {
			Files.walkFileTree(Paths.get(SavePaths.getProjectFolder()+"ProjectWithDuplicateBetweenFiles"+"/src/"), new SimpleFileVisitor<Path>() {
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
		System.out.println(foundLocs.rascalLocList());
	}

}
