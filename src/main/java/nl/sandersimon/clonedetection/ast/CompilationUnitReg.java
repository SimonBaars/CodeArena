package nl.sandersimon.clonedetection.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;

import nl.sandersimon.clonedetection.model.ListMap;

public class CompilationUnitReg {
	private int lastLineNumber = 0;
	private final ListMap<Integer, Node> thisFile = new ListMap<>();
	private final LineBuffer buffer;
	
	public CompilationUnitReg(int size) {
		this.buffer = new LineBuffer(size);
	}
	
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

	public ListMap<Integer, Node> getThisFile() {
		return thisFile;
	}

	public LineBuffer getBuffer() {
		return buffer;
	}
}
