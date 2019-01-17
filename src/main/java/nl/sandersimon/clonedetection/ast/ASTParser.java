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
import scala.actors.threadpool.Arrays;

public class ASTParser {
	private static final int AMOUNT_OF_LINES = 6;
	private static final double SIMILARITYTHRESHOLD = 90;
	
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
				e.printStackTrace();
			}
		}
		//System.out.println(Arrays.toString(foundCloneClasses.toArray()));
	}

	private static void scanForClones(List<CloneClass> potentialClones, List<CloneClass> foundCloneClasses, LineBuffer buffer) {
		for(CloneClass potentialClone : potentialClones) {
			if(similarity(potentialClone.getCloneClass(), buffer.getLines())>=SIMILARITYTHRESHOLD) {
				foundCloneClasses.add(new CloneClass(buffer.getLines()));
			}
		}
	}

	private static double similarity(List<List<Node>> leftClone, List<List<Node>> rightClone) {
		final List<Node> left = new FlattenedList<>(leftClone);
		final List<Node> right = new FlattenedList<>(rightClone);
		SimilarityReg r = new SimilarityReg();
		final int leftSize = left.size();
		final int rightSize = right.size();
		for(int i = 0; i<Math.max(leftSize, rightSize); i++) {
			Node leftLine = i<leftSize ? left.get(i) : null;
			Node rightLine = i<rightSize ? right.get(i): null;
			boolean checkEqual = true;
			if(currentToken(r, i, leftLine, rightLine, true)) {
				r.incrementSame(1);
				r.incrementDifferent(1);
				checkEqual = false;
			}
			if(currentToken(r, i, leftLine, rightLine, false)) {
				r.incrementSame(1);
				if(checkEqual)
					r.decementDifferent();
				else r.incrementDifferent(1);
				checkEqual = false;
			}
			if(checkEqual) {
				if(leftLine == rightLine){
					r.incrementSame(2);
				} else {
					r.incrementDifferent(2); //Add 2 because on both sides (left and right) a different is found.
					r.putLeftBuff(i, leftLine);
					r.putRightBuff(i, rightLine);
				}
			}
		}
		//System.out.println("Same = "+r.getSame()+", Different = "+r.getDifferent()+", DiffPoints = "+r.getDiffPoints()+", percSame = "+(r.getSame()/((double)(r.getSame()+r.getDifferent()))*100D));
		return r.getSame()/((double)(r.getSame()+r.getDifferent()))*100D;
	}

	private static boolean currentToken(SimilarityReg r, int i, Node leftLine, Node rightLine, boolean isLeft) {
		Map<Integer,Node> thisMap = isLeft ? r.getRightBuff() : r.getLeftBuff();
		Node relevantLine = isLeft ? leftLine : rightLine;
		if(relevantLine == null)
			return false;
		for(Entry<Integer, Node> e : thisMap.entrySet()) { // TODO: This can be replaced by map access for O(n) performance.
			if(e.getValue().equals(relevantLine)) {
				r.incrementDiffPoints(i-e.getKey());
				thisMap.remove(e.getKey());
				if(isLeft) {
					r.putRightBuff(i, rightLine);
				} else {
					r.putLeftBuff(i, leftLine);
				}
				r.decementDifferent();
				return true;
			}
		}
		return false;
	}
}
