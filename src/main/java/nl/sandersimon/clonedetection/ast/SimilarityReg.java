package nl.sandersimon.clonedetection.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.Node;

public class SimilarityReg {
	private final Map<Integer, Node> leftBuff = new HashMap<>();
	private final Map<Integer, Node> rightBuff = new HashMap<>();
	private int same = 0;
	private int different = 0;
	private int diffPoints = 0;
	
	public int getSame() {
		return same;
	}
	public void setSame(int same) {
		this.same = same;
	}
	public int getDifferent() {
		return different;
	}
	public void setDifferent(int different) {
		this.different = different;
	}
	public int getDiffPoints() {
		return diffPoints;
	}
	public void setDiffPoints(int diffPoints) {
		this.diffPoints = diffPoints;
	}
	public Map<Integer, Node> getLeftBuff() {
		return leftBuff;
	}
	public Map<Integer, Node> getRightBuff() {
		return rightBuff;
	}
	public void incrementSame() {
		same++;
	}
	public void incrementDiffPoints(int i) {
		diffPoints+=i;
	}
	public void incrementDifferent() {
		different++;
	}
	public void decementDifferent() {
		different--;
	}
}
