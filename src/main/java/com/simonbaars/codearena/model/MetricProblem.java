package com.simonbaars.codearena.model;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.PackageDeclaration;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.metrics.enums.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.codearena.editor.CodeEditorMaker;

public class MetricProblem implements Comparable<MetricProblem>, RequiresNodeContext {

	private int problemSize;
	private ProblemType type;
	private Sequence seq;
	
	public MetricProblem(ProblemType problem, int problemSize, Sequence seq) {
		super();
		this.type = problem;
		this.problemSize = problemSize;
		this.seq = seq;
	}

	public MetricProblem() {
		super();
	}

	public int getProblemSize() {
		return problemSize;
	}

	public void setProblemSize(int lines) {
		this.problemSize = lines;
	}

	public List<Location> getLocations() {
		return seq.getSequence();
	}

	@Override
	public int compareTo(MetricProblem o) {
		return Integer.compare(volume(), o.volume());
	}

	public void add(Location construct) {
		seq.add(construct);
	}
	
	public int size() {
		return seq.size();
	}

	public Location get(int j) {
		return seq.getSequence().get(j);
	}
	
	public int volume() {
		return problemSize;
	}

	public void open() {
		CodeEditorMaker.create(this);
	}

	public String getName() {
		if(size() == 0)
			return "error";
		return get(0).getName();
	}

	public String getPackage() {
		if(size() == 0)
			return "error";
		Optional<PackageDeclaration> p = getCompilationUnit(get(0).getContents().getNodes().get(0)).getPackageDeclaration();
		return "No package";
	}

	public String getMetric() {
		return type.getName();
	}

	public ProblemType getType() {
		return type;
	}

	public void setType(ProblemType type) {
		this.type = type;
	}

	public Sequence getSeq() {
		return seq;
	}

	public void setSeq(Sequence seq) {
		this.seq = seq;
	}
}
