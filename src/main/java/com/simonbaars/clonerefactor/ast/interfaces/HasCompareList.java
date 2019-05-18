package com.simonbaars.clonerefactor.ast.interfaces;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.compare.Compare;
import com.simonbaars.clonerefactor.compare.CompareToken;
import com.simonbaars.clonerefactor.settings.Settings;

public interface HasCompareList extends DeterminesNodeTokens {
	public List<Compare> getCompare();
	
	public default void createComparablesByNode(List<JavaToken> myTokens, Node statement) {
		Map<Range, Node> compareMap = getNodesForCompare(Collections.singletonList(statement), getRange(myTokens));
		myTokens.forEach(token -> {
			Optional<Entry<Range, Node>> thisNodeOptional = compareMap.entrySet().stream().filter(e -> e.getKey().contains(token.getRange().get())).findAny();
			if(thisNodeOptional.isPresent()) {
				if(thisNodeOptional.get().getValue()!=null)
					createCompareFromNode(compareMap, token, thisNodeOptional.get());
			} else getCompare().add(Compare.create(token, token, Settings.get().getCloneType()));
		});
	}
	
	public default void createCompareFromNode(Map<Range, Node> compareMap, JavaToken token, Entry<Range, Node> thisNode) {
		Compare createdNode = Compare.create(thisNode.getValue(), token, Settings.get().getCloneType());
		getCompare().add(createdNode);
		getCompare().addAll(createdNode.relevantChildren(this));
		if(createdNode instanceof CompareToken) compareMap.remove(thisNode.getKey()); 
		else thisNode.setValue(null);
	}
	
	public default Map<Range, Node> getNodesForCompare(List<? extends Node> parents, Range r){
		return getNodesForCompare(parents, new HashMap<>(), r);
	}
	
	public default Map<Range, Node> getNodesForCompare(List<? extends Node> parents, Map<Range, Node> nodes, Range range){
		for(Node node : parents) {
			if(node.getRange().isPresent()) {
				Range r = node.getRange().get();
				if(range.contains(r) && Compare.comparingNode(node))
					nodes.put(r, node);
				else if (r.begin.isAfter(range.end))
					return nodes;
				if(!nodes.containsKey(r))
					getNodesForCompare(node.getChildNodes(), nodes, range);
			}
		}
		return nodes;
	}
}
