package nl.sandersimon.clonedetection.ast;

import java.util.Arrays;

import com.github.javaparser.ast.Node;

public class LineBuffer {
	int currentIndex = 0;
	private final Node[] lines;
	
	public LineBuffer(int size) {
		lines = new Node[size];
	}
	
	public void addToBuffer(Node n) {
		lines[currentIndex] = n;
		currentIndex++;
		if(currentIndex>=lines.length)
			currentIndex=0;
	}
	
	public boolean isValid() {
		return Arrays.stream(lines).noneMatch(e -> e == null);
	}
}
