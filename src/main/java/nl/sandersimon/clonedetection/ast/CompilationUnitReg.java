package nl.sandersimon.clonedetection.ast;

import java.util.List;

import com.github.javaparser.ast.Node;

public class CompilationUnitReg {
	private int lastLineNumber = 0;

	public int getLastLineNumber() {
		return lastLineNumber;
	}

	public void setLastLineNumber(int lastLineNumber) {
		this.lastLineNumber = lastLineNumber;
	}
	
	public boolean lastLineNumberExists() {
		return lastLineNumber>0;
	}

	public void visitLine(int line) {
		this.lastLineNumber = line;
	}
}
