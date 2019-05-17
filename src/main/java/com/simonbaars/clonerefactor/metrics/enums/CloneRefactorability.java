package com.simonbaars.clonerefactor.metrics.enums;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class CloneRefactorability implements MetricEnum<Refactorability>, RequiresNodeOperations {
	public enum Refactorability{
		CANBEEXTRACTED,
		NOEXTRACTIONBYCONTENTTYPE,
		PARTIALBLOCK,
		COMPLEXCONTROLFLOW,
	}

	@Override
	public Refactorability get(Sequence sequence) {
		if(new CloneContents().get(sequence)!=CloneContents.ContentsType.PARTIALMETHOD)
			return Refactorability.NOEXTRACTIONBYCONTENTTYPE;
		if(sequence.getSequence().stream().anyMatch(e -> e.getContents().getNodes().stream().anyMatch(f -> complexControlFlow(f))))
			return Refactorability.COMPLEXCONTROLFLOW;
		for(Location location : sequence.getSequence()) {
			for(Node n : location.getContents().getNodes()) {
				List<Node> children = childrenToParse(n);
				if(children.stream().anyMatch(e -> !isExcluded(e) && !location.getContents().getNodes().contains(e))) {
					return Refactorability.PARTIALBLOCK;
				}
			}
		}
		return Refactorability.CANBEEXTRACTED;
	}
	
	private boolean complexControlFlow(Node n) {
		return n instanceof BreakStmt || n instanceof ReturnStmt || n instanceof ContinueStmt;
	}
}
