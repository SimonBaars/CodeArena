package nl.sandersimon.clonedetection.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.Node;
import com.github.javaparser.metamodel.NodeMetaModel;

public class SimilarityReg {
	private final Map<Integer, NodeMetaModel> leftBuff = new HashMap<>();
	private final Map<Integer, NodeMetaModel> rightBuff = new HashMap<>();
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
	public Map<Integer, NodeMetaModel> getLeftBuff() {
		return leftBuff;
	}
	public Map<Integer, NodeMetaModel> getRightBuff() {
		return rightBuff;
	}
	public void incrementSame(int i) {
		same+=i;
	}
	public void incrementDiffPoints(int i) {
		diffPoints+=i;
	}
	public void incrementDifferent(int i) {
		different+=i;
	}
	public void decementDifferent() {
		different--;
	}
	public void putLeftBuff(int i, NodeMetaModel leftLine) {
		if(leftLine!=null)
			leftBuff.put(i, leftLine);
	}
	
	public void putRightBuff(int i, NodeMetaModel rightLine) {
		if(rightLine!=null)
			rightBuff.put(i, rightLine);
	}
}
