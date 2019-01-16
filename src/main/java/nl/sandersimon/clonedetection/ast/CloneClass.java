package nl.sandersimon.clonedetection.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;

public class CloneClass {
	private final List<List<Node>> cloneClass;
	
	public CloneClass(int size) {
		cloneClass = new ArrayList<>(size);
	}

	public CloneClass(List<List<Node>> lines) {
		cloneClass = new ArrayList<>(lines);
	}

	public List<List<Node>> getCloneClass() {
		return cloneClass;
	}
}
