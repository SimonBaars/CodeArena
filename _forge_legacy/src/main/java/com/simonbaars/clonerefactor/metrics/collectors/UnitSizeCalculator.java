package com.simonbaars.clonerefactor.metrics.collectors;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.model.FiltersTokens;

public class UnitSizeCalculator implements Calculator<MethodDeclaration>, FiltersTokens {
    @Override
    public int calculate(MethodDeclaration method) {
    	Set<Integer> lines = new HashSet<>();
    	findAll(lines, method.getChildNodes());
        return lines.size();
    }

	private void findAll(Set<Integer> lines, List<Node> childNodes) {
		for(Node child : childNodes) {
			if(child.getTokenRange().isPresent())
				StreamSupport.stream(child.getTokenRange().get().spliterator(), false).filter(e -> isComparableToken(e) && e.getRange().isPresent()).forEach(e -> lines.add(e.getRange().get().begin.line));
			findAll(lines, child.getChildNodes());
		}
	}
}