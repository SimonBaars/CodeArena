package nl.sandersimon.clonedetection.ast;

import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.Node;

public class LineBuffer {
	int currentIndex = 0;
	private final List<Node>[] lines;
	
	public LineBuffer(int size) {
		lines = new List<Node>[size];
	}
	
	public void addToBuffer(List<Node> n) {
		lines[currentIndex] = n;
		currentIndex++;
		if(currentIndex>=lines.length)
			currentIndex=0;
	}
	
	public boolean isValid() {
		return Arrays.stream(lines).noneMatch(e -> e == null);
	}
}
