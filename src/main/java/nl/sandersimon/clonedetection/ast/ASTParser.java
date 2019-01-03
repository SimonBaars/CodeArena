package nl.sandersimon.clonedetection.ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

public class ASTParser {
	private static Map<File, Map<Integer, Node>> tokensOnLine = new HashMap<>();
	private static List<Integer> sortedDomain = new ArrayList<>();
	
	public static void parse(File[] path) {
		for(File file : path) {
			try {
				Map<Integer, Node> thisFile = new HashMap<>();
				CompilationUnit cu = JavaParser.parse(file);
				CompilationUnitReg r = new CompilationUnitReg();
				cu.stream().forEach(t -> {
					int line = t.getRange().get().begin.line;
					if(r.lastLineNumberExists() && r.getLastLineNumber()!=line) {
						
					}
					r.visitLine(line);
					thisFile.put(line, t);
				});
				tokensOnLine.put(file, thisFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
