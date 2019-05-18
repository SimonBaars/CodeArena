package com.simonbaars.clonerefactor.metrics;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.SetMap;
import com.simonbaars.clonerefactor.metrics.enums.CloneContents;
import com.simonbaars.clonerefactor.metrics.enums.CloneLocation;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class MetricCollector {
	private final SetMap<Path, Integer> parsedEffectiveLines = new SetMap<>();
	private final SetMap<Path, Integer> parsedLines = new SetMap<>();
	private final SetMap<Path, Range> parsedTokens = new SetMap<>();
	private final SetMap<Path, Range> parsedNodes = new SetMap<>();
	private final Metrics metrics = new Metrics();
	private final CloneRelation relationFinder = new CloneRelation();
	private final CloneLocation locationFinder = new CloneLocation();
	private final CloneContents contentsFinder = new CloneContents();
	private final CloneRefactorability extractFinder = new CloneRefactorability();
	
	public MetricCollector() {}
	
	public void reportFoundNode(Location l) {
		//System.out.println(l.getContents().toString());
		//System.out.println(l.getContents().getEffectiveTokenTypes());
		//System.out.println(l.getContents().getNodeTypes());
		metrics.totalAmountOfLines+=getUnparsedLines(l, false);
		metrics.totalAmountOfNodes+=l.getAmountOfNodes();
		metrics.totalAmountOfTokens+=l.getAmountOfTokens();
		metrics.totalAmountOfEffectiveLines+=getUnparsedEffectiveLines(l, false);
		l.getContents().getNodes().forEach(e -> relationFinder.registerNode(e));
	}
	
	private int getUnparsedEffectiveLines(Location l, boolean countOverlap) {
		int amountOfLines = 0;
		for(Integer i : l.getContents().getEffectiveLines()) {
			Set<Integer> lines = parsedEffectiveLines.get(l.getFile());
			if(!lines.contains(i)) {
				amountOfLines++;
				lines.add(i);
			} else if(countOverlap) metrics.overlappingEffectiveLines++;
		}
		return amountOfLines;
	}
	
	private int getUnparsedLines(Location l, boolean countOverlap) {
		int amountOfLines = 0;
		for(int i = l.getRange().begin.line; i<=l.getRange().end.line; i++) {
			Set<Integer> lines = parsedLines.get(l.getFile());
			if(!lines.contains(i)) {
				amountOfLines++;
				lines.add(i);
			} else if(countOverlap) metrics.overlappingLines++;
		}
		return amountOfLines;
	}
	
	public Metrics reportClones(List<Sequence> clones) {
		parsedLines.clear();
		parsedEffectiveLines.clear();
		for(Sequence clone : clones)
			reportClone(clone);
		relationFinder.clearClasses();
		return metrics;
	}

	private void reportClone(Sequence clone) {
		metrics.amountPerCloneClassSize.increment(clone.size());
		metrics.amountPerNodes.increment(clone.getNodeSize());
		metrics.amountPerTotalNodeVolume.increment(clone.getTotalNodeVolume());
		metrics.amountPerEffectiveLines.increment(clone.getEffectiveLineSize());
		metrics.amountPerTotalEffectiveLineVolume.increment(clone.getTotalEffectiveLineVolume());
		clone.setMetrics(relationFinder, extractFinder);
		metrics.amountPerRelation.increment(clone.getRelationType());
		metrics.amountPerExtract.increment(clone.getRefactorability());
		for(Location l : clone.getSequence()) {
			reportClonedLocation(l);
		}
	}

	private void reportClonedLocation(Location l) {
		metrics.amountOfLinesCloned+=getUnparsedLines(l, true);
		metrics.amountOfTokensCloned+=getUnparsedTokens(l, true);
		metrics.amountOfNodesCloned+=getUnparsedNodes(l, true);
		metrics.amountOfEffectiveLinesCloned+=getUnparsedEffectiveLines(l, true);
		l.setMetrics(locationFinder);
		l.getContents().setMetrics(contentsFinder);
		metrics.amountPerLocation.increment(l.getLocationType());
		metrics.amountPerContents.increment(l.getContents().getContentsType());
	}

	private int getUnparsedTokens(Location l, boolean countOverlap) {
		int amount = 0;
		for(JavaToken n : l.getContents().getTokens()) {
			Range r = n.getRange().get();
			if(!parsedTokens.get(l.getFile()).contains(r)) {
				parsedTokens.addTo(l.getFile(), r);
				amount++;
			} else if(countOverlap) metrics.overlappingTokens++;
		}
		return amount;
	}

	private int getUnparsedNodes(Location l, boolean countOverlap) {
		int amount = 0;
		for(Node n : l.getContents().getNodes()) {
			Range r = n.getRange().get();
			if(!parsedNodes.get(l.getFile()).contains(r)) {
				parsedNodes.addTo(l.getFile(), r);
				amount++;
			} else if(countOverlap) metrics.overlappingNodes++;
		}
		return amount;
	}

	public Metrics getMetrics() {
		return metrics;
	}
}
