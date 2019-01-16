package nl.sandersimon.clonedetection.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;

public class CloneClass {
	private final List<List<List<Node>>> cloneClass = new ArrayList<>();

	public List<List<List<Node>>> getCloneClass() {
		return cloneClass;
	}
}
