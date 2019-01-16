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
	private final static int AMOUNT_OF_LINES = 6;
	
	public static void parse(File[] path) {
		final Map<File, ListMap<Integer, Node>> tokensOnLine = new HashMap<>();
		final ListMap<File, Integer> sortedDomain = new ListMap<>();
		final Map<File, List<List<List<Node>>>> potentialClones = new HashMap<>();
		for(File file : path) {
			try {
				final CompilationUnit cu = JavaParser.parse(file);
				final CompilationUnitReg r = new CompilationUnitReg(AMOUNT_OF_LINES);
				for (Iterator<Node> it = cu.stream().iterator(); it.hasNext();) {
					Node t = it.next();
					int line = t.getRange().get().begin.line;
					if(!it.hasNext() || (r.lastLineNumberExists() && r.getLastLineNumber()!=line)) {
						int finishedLine = line; // Line to be scanned for clones
						if(it.hasNext())
							finishedLine = r.getLastLineNumber();
						sortedDomain.addTo(file, finishedLine);
						List<Node> nodes = r.getThisFile().get(finishedLine);
						r.getBuffer().addToBuffer(nodes);
						if(r.getBuffer().isValid()) {
							r.getPotentialClones().add(new ArrayList<>(r.getBuffer().getLines()));
							scanForClones(potentialClones, r.getBuffer());
						}
					}
					r.visitLine(line);
					r.getThisFile().addTo(line, t);
					potentialClones.put(file, r.getPotentialClones());
				}
				tokensOnLine.put(file, r.getThisFile());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void scanForClones(Map<File, List<List<List<Node>>>> potentialClones, LineBuffer buffer) {
		// TODO Auto-generated method stub
		
	}
}
