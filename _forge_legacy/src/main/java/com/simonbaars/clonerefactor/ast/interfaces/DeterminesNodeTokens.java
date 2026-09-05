package com.simonbaars.clonerefactor.ast.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithImplements;
import com.simonbaars.clonerefactor.model.FiltersTokens;

public interface DeterminesNodeTokens extends FiltersTokens, RequiresNodeOperations {
	public default List<JavaToken> calculateTokensFromNode(Node n) {
		Range validRange = getValidRange(n);
		List<JavaToken> tokens = new ArrayList<>();
		for(JavaToken token : n.getTokenRange().get()) {
			Optional<Range> r = token.getRange();
			if(r.isPresent()) {
				if(!validRange.contains(r.get())) break;
				if(isComparableToken(token)) tokens.add(token);
				if(n instanceof NodeWithImplements && token.asString().equals("{")) break; // We cannot exclude the body of class files, this is a workaround.
			}
		}
		return tokens;
	}
	
	public default Range getValidRange(Node n) {
		Range nodeRange = n.getRange().get();
		for(ListIterator<Node> it = n.getChildNodes().listIterator(n.getChildNodes().size()); it.hasPrevious(); ) {
			Node node = it.previous();
			if(!isExcluded(node) && node.getRange().isPresent()) {
				nodeRange = new Range(nodeRange.begin, getPosition(node.getRange().get().begin, nodeRange.begin));
			} else break;
		}
		return nodeRange;
	}
	
	public default Position getPosition(Position pos, Position begin) {
		if(pos.equals(begin))
			return pos;
		return new Position(pos.line, pos.column-1);
	}
	
	public default Range getRange(List<JavaToken> tokens) {
		return new Range(tokens.get(0).getRange().get().begin, tokens.get(tokens.size()-1).getRange().get().end);
	}
	
	public default Range getRange(Node n) {
		return getRange(calculateTokensFromNode(n));
	}
}
