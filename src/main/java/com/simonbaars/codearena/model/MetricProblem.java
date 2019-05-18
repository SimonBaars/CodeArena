package com.simonbaars.codearena.model;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.PackageDeclaration;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.metrics.enums.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.codearena.CloneDetection;
import com.simonbaars.codearena.editor.CodeEditorMaker;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MetricProblem implements Comparable<MetricProblem>, RequiresNodeContext {

	private String metric;
	private int lines;
	private ProblemType type;
	private Sequence seq;
	
	public MetricProblem(String metric, int lines) {
		super();
		this.metric = metric;
		this.lines = lines;
	}

	public MetricProblem() {
		super();
	}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
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
		return lines * size();
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
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}
}
