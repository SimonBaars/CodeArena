import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nl.sandersimon.clonedetection.ast.ASTParser;
import nl.sandersimon.clonedetection.common.SavePaths;

public class ASTTest {
	@Test
	public void testNewAST() throws IOException {
		String project = "ProjectWithDuplicateBetweenFiles";
		List<File> files = new ArrayList<>();
		Files.walkFileTree(Paths.get(SavePaths.getProjectFolder()+project+"/src/"), new SimpleFileVisitor<Path>() {
				    @Override
				    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
				    	File file = filePath.toFile();
						if(file.getName().endsWith(".java"))
				    		files.add(file);
				    	return FileVisitResult.CONTINUE;
				    }
				});
		ASTParser.parse(files.toArray(new File[0]));
	}
}