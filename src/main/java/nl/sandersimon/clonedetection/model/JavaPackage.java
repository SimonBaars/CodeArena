package nl.sandersimon.clonedetection.model;

import java.util.ArrayList;
import java.util.List;

public class JavaPackage implements Comparable<JavaPackage> {
	private String packagePath;
	private List<JavaClass> classes = new ArrayList<JavaClass>();
	
	public JavaPackage() {
	}

	@Override
	public int compareTo(JavaPackage o) {
		return Integer.compare(getProblem(), o.getProblem());
	}
	
	public int getProblem() {
		return classes.stream().mapToInt(JavaClass::getProblems).sum();
	}

}
