package nl.sandersimon.clonedetection.ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import nl.sandersimon.clonedetection.model.ListMap;

public class ASTParser {
	private final static int AMOUNT_OF_LINES = 6;
	private final static float SIMILARITYTHRESHOLD = 90;
	
	public static void parse(File[] path) {
		final Map<File, ListMap<Integer, Node>> tokensOnLine = new HashMap<>();
		final ListMap<File, Integer> sortedDomain = new ListMap<>();
		// Lists in order: List of potential clone classes -> 6 lines in the potential clone class -> List of tokens on the line.
		final List<CloneClass> potentialClones = new ArrayList<>();
		final List<CloneClass> foundCloneClasses = new ArrayList<>();
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
							scanForClones(potentialClones, foundCloneClasses, r.getBuffer());
							potentialClones.add(new CloneClass(r.getBuffer().getLines()));
						}
					}
					r.visitLine(line);
					r.getThisFile().addTo(line, t);
				}
				tokensOnLine.put(file, r.getThisFile());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void scanForClones(List<CloneClass> potentialClones, List<CloneClass> foundCloneClasses, LineBuffer buffer) {
		for(CloneClass potentialClone : potentialClones) {
			if(similarity(potentialClone.getCloneClass(), buffer.getLines())>=SIMILARITYTHRESHOLD) {
				foundCloneClasses.add(new CloneClass(buffer.getLines()));
			}
		}
	}

	private static double similarity(List<List<Node>> left, List<List<Node>> right) {
		SimilarityReg r = new SimilarityReg();
		for(int i = 0; i<Math.max(left.size(), right.size()); i++) {
			List<Node> leftLine = i<left.size() ? left.get(i) : null;
			List<Node> rightLine = i<right.size() ? right.get(i): null;
			currentToken(r, i, rightLine);
			/*if(rightBuff.values().contains(leftLine)) {
				
				same++;
				diffPoints += 
				rightBuff.remove(leftLine);
			}
			if(leftBuff.contains(rightLine)) {
				leftBuff.remove(rightLine);
			}
			if(leftLine == rightLine) {
				same++;
			}*/
		}
		return 0;
	}

	private static void currentToken(SimilarityReg r, int i, List<Node> rightLine) {
		for(Entry<Integer, List<Node>> e : r.getLeftBuff().entrySet()) {
			if(e.getValue().equals(rightLine)) {
				r.incrementSame();
				r.incrementDiffPoints(i-e.getKey());
				r.getLeftBuff().remove(e.getKey());
				break;
			}
		}
	}
}
