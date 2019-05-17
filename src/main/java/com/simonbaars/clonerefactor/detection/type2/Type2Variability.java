package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.compare.Compare;
import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.interfaces.ChecksThresholds;
import com.simonbaars.clonerefactor.detection.interfaces.RemovesDuplicates;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class Type2Variability implements CalculatesPercentages, ChecksThresholds, RemovesDuplicates {
	public List<Sequence> determineVariability(Sequence s) {
		List<List<Compare>> literals = createLiteralList(s);
		int[][] equalityArray = createEqualityArray(literals);
		if(globalThresholdsMet(equalityArray, s.getSequence().stream().mapToInt(e -> e.getContents().getTokens().size()).sum())) // We first check the thresholds for the entire sequence. If those are not met, we will try to create smaller sequences
			return Collections.singletonList(s);
		return findAllValidSubSequences(s, literals, equalityArray);
	}

	private List<Sequence> findAllValidSubSequences(Sequence s, List<List<Compare>> literals, int[][] equalityArray) {
		List<Sequence> sequences = new ArrayList<>();
		Map<Integer, int[][]> statementEqualityArrays = findConnectedStatements(s, literals, equalityArray);
		for(int[] relevantLocationIndices : powerset(IntStream.range(0, s.size()).toArray())){
			if(relevantLocationIndices.length>1)
				sliceSequence(sequences, s, statementEqualityArrays, relevantLocationIndices);
		}
		return sequences;
	}
	
	// https://stackoverflow.com/questions/40201309/best-way-to-get-a-power-set-of-an-array
	public int[][] powerset(int[] a){
		int max = 1 << a.length;
		int[][] result = new int[max][];
		for (int i = 0; i < max; ++i) {
		    result[i] = new int[Integer.bitCount(i)];
		    for (int j = 0, b = i, k = 0; j < a.length; ++j, b >>= 1)
		        if ((b & 1) != 0)
		            result[i][k++] = a[j];
		}
		return result;
	}
	
	private void sliceSequence(List<Sequence> sequences, Sequence s, Map<Integer, int[][]> statementEqualityArrays, int[] relevantLocationIndices) {
		List<WeightedPercentage> calcPercentages = getWeightedPercentages(s, statementEqualityArrays, relevantLocationIndices);
		List<WeightedPercentage> percentagesList = new ArrayList<>();
		for(int i = 0; i<calcPercentages.size(); i++) {
			percentagesList.add(calcPercentages.get(i));
			checkCloneValidity(sequences, s, relevantLocationIndices, calcPercentages, percentagesList, i);
		}
	}

	private void checkCloneValidity(List<Sequence> sequences, Sequence s, int[] relevantLocationIndices,
			List<WeightedPercentage> calcPercentages, List<WeightedPercentage> percentagesList, int i) {
		boolean notValidRegardingVariability = calcAvg(percentagesList) > Settings.get().getType2VariabilityPercentage();
		if((notValidRegardingVariability && !canFixIt(calcPercentages, percentagesList, i)) || i+1 == calcPercentages.size()) {
			if(percentagesList.size()>1) {
				if(notValidRegardingVariability) percentagesList.remove(percentagesList.size()-1);
				Sequence newSeq = createSequence(s, calcPercentages.indexOf(percentagesList.get(0)), calcPercentages.indexOf(percentagesList.get(percentagesList.size()-1)), relevantLocationIndices);
				if(checkThresholds(newSeq) && removeDuplicatesOf(sequences, newSeq)) 
					sequences.add(newSeq);
			}
			percentagesList.clear();
		}
	}

	private Sequence createSequence(Sequence s, int from, int to, int[] relevantIndices) {
		Sequence newSeq = new Sequence();
		for(int locationIndex : relevantIndices) {
			Location l = s.getSequence().get(locationIndex);
			Location l2 = new Location(l);
			newSeq.add(l2);
			List<Node> myNodes = l2.getContents().getNodes();
			for(int nodeIndex = myNodes.size()-1; nodeIndex>=0; nodeIndex--)
				if(nodeIndex<from || nodeIndex>to)
					myNodes.remove(nodeIndex);
			Range r = new Range(myNodes.get(0).getRange().get().begin, findNodeLocation(getStatementLoc(l2), myNodes.get(myNodes.size()-1)).getRange().end);
			l2.setRange(r);
			l2.getContents().setRange(r);
			l2.getContents().stripToRange();
		}
		return newSeq;
	}
	
	public Location getStatementLoc(Location l) {
		if(l.getNextLine() != null)
			return l.getNextLine().getPrevLine();
		return l.getPrevLine().getNextLine();
	}
	
	private Location findNodeLocation(Location l, Node n) {
		if(l.getContents().getNodes().get(0) == n)
			return l;
		return findNodeLocation(l.getNextLine(), n);
	}

	private List<WeightedPercentage> getWeightedPercentages(Sequence s, Map<Integer, int[][]> statementEqualityArrays, int[] relevantLocationIndices) {
		List<WeightedPercentage> calcPercentages = new ArrayList<>();
		for(int currNodeIndex = 0; currNodeIndex<s.getAny().getContents().getNodes().size(); currNodeIndex++) {
			int[][] equality = statementEqualityArrays.get(currNodeIndex);
			calcPercentages.add(new WeightedPercentage(diffPerc(equality, relevantLocationIndices), equality[0].length));
		}
		return calcPercentages;
	}

	private boolean canFixIt(List<WeightedPercentage> calcPercentages, List<WeightedPercentage> percentagesList, int i) {
		percentagesList = new ArrayList<>(percentagesList);
		for(i++; i<calcPercentages.size(); i++) {
			percentagesList.add(calcPercentages.get(i));
			if(calcAvg(percentagesList) <= Settings.get().getType2VariabilityPercentage()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a per statement equality array.
	 * @param s
	 * @param equalityArray
	 * @param relevantIndices 
	 * @return
	 */
	private Map<Integer, int[][]> findConnectedStatements(Sequence s, List<List<Compare>> literals, int[][] equalityArray) {
		Map<Integer, int[][]> statementEqualityArrays = new HashMap<>();
		for(int currNodeIndex = 0, startCompareIndex = 0, currCompareIndex = 0; currNodeIndex<s.getAny().getContents().getNodes().size(); currNodeIndex++) {
			for(;currCompareIndex<literals.get(0).size() && getLocationForNode(s.getAny(), currNodeIndex).getRange().contains(literals.get(0).get(currCompareIndex).getRange()); currCompareIndex++);
			statementEqualityArrays.put(currNodeIndex, new int[s.size()][currCompareIndex-startCompareIndex]);
			for(int locationIndex = 0; locationIndex<s.size(); locationIndex++) {
				for(int compareIndex = startCompareIndex; compareIndex<currCompareIndex; compareIndex++) {
					statementEqualityArrays.get(currNodeIndex)[locationIndex][compareIndex-startCompareIndex] = equalityArray[locationIndex][compareIndex];
				}
			}
			startCompareIndex = currCompareIndex;
		}
		return statementEqualityArrays;
	}
	
	public Location getLocationForNode(Location l, int node) {
		return getLocation(getStatementLoc(l), node);
	}

	private Location getLocation(Location l, int node) {
		if(node <= 0)
			return l;
		return getLocation(l.getNextLocation(), node-1);
	}

	private boolean globalThresholdsMet(int[][] equalityArray, int total) {
		return diffPerc(equalityArray)<=Settings.get().getType2VariabilityPercentage();
	}

	private int[][] createEqualityArray(List<List<Compare>> literals) {
		int[][] equalityArray = new int[literals.size()][literals.get(0).size()];
		for(int j = 0; j<literals.get(0).size(); j++) {
			final List<Compare> differentCompareLiterals = new ArrayList<>();
			int curr = 0;
			for(int i = 0; i<literals.size(); i++) {
				int index = differentCompareLiterals.indexOf(literals.get(i).get(j));
				if(index == -1) {
					equalityArray[i][j] = curr++;
					differentCompareLiterals.add(literals.get(i).get(j));
				} else {
					equalityArray[i][j] = index;
				}
			}
		}
		return equalityArray;
	}

	private List<List<Compare>> createLiteralList(Sequence s) {
		List<List<Compare>> literals = new ArrayList<>();
		for(Location l : s.getSequence()) {
			List<Compare> literals2 = l.getContents().getType2Comparables();
			literals.add(literals2);
			literals2.forEach(e -> e.setCloneType(CloneType.TYPE1));
		}
		return literals;
	}
}
