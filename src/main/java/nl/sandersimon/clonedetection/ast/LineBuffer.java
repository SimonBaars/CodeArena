package nl.sandersimon.clonedetection.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.Node;

public class LineBuffer {
	int currentIndex = 0;
	private final List<List<Node>> lines;
	
	public LineBuffer(int size) {
		lines = new ArrayList<>(size);
		for(int i = 0; i<size; i++)
			lines.add(null);
	}
	
	public void addToBuffer(List<Node> n) {
		lines.set(currentIndex, n);
		currentIndex++;
		if(currentIndex>=lines.size())
			currentIndex=0;
	}
	
	public boolean isValid() {
		return lines.stream().noneMatch(e -> e == null);
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public List<List<Node>> getLines() {
		return lines;
	}
}
