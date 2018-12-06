package nl.sandersimon.clonedetection.model;

public class JavaClass implements Comparable<JavaClass> {
	private String className;
	private int problems = 0;
	
	@Override
	public int compareTo(JavaClass o) {
		return Integer.compare(problems, o.problems);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getProblems() {
		return problems;
	}

	public void setProblems(int problems) {
		this.problems = problems;
	}
	
	
}
