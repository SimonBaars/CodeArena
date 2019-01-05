package nl.sandersimon.clonedetection.ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import nl.sandersimon.clonedetection.model.ListMap;

public class ASTParser {
	private static Map<File, ListMap<Integer, Node>> tokensOnLine = new HashMap<>();
	private static List<Integer> sortedDomain = new ArrayList<>();
	
	public static void parse(File[] path) {
		for(File file : path) {
			try {
				ListMap<Integer, Node> thisFile = new ListMap<>();
				CompilationUnit cu = JavaParser.parse(file);
				CompilationUnitReg r = new CompilationUnitReg();
				for (Iterator<Node> it = cu.stream().iterator(); it.hasNext();) {
					Node t = it.next();
					int line = t.getRange().get().begin.line;
					if(!it.hasNext() || (r.lastLineNumberExists() && r.getLastLineNumber()!=line)) {
						int finishedLine = line;
						if(it.hasNext())
							finishedLine = r.getLastLineNumber();
						sortedDomain.add(finishedLine);
						List<Node> nodes = thisFile.get(finishedLine);
					}
					r.visitLine(line);
					thisFile.addTo(line, t);
				}
				tokensOnLine.put(file, thisFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
