package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.simonbaars.clonerefactor.metrics.Metrics;

public class DetectionResults {
	private Metrics metrics;
	private List<Sequence> clones;
	
	public DetectionResults(Metrics metrics, List<Sequence> clones) {
		super();
		this.metrics = metrics;
		this.clones = clones;
	}
	
	public DetectionResults() {
		super();
		this.metrics = new Metrics();
		this.clones = new ArrayList<>();
	}
	
	public Metrics getMetrics() {
		return metrics;
	}
	
	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}
	
	public List<Sequence> getClones() {
		return clones;
	}
	
	public void setClones(List<Sequence> clones) {
		this.clones = clones;
	}

	@Override
	public String toString() {
		return "DetectionResults [metrics=" + metrics + "\n"
				+ "clones=" + Arrays.toString(clones.toArray()).replace("Location [", "\nLocation [").replace("Sequence [sequence=[", "\nSequence [sequence=[") + "]";
	}

	public DetectionResults sorted() {
		Collections.sort(getClones());
		return this;
	}
	
	
}
