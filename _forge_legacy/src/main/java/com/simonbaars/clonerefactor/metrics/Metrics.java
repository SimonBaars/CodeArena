package com.simonbaars.clonerefactor.metrics;

import com.simonbaars.clonerefactor.datatype.CountMap;
import com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType;
import com.simonbaars.clonerefactor.metrics.enums.CloneLocation.LocationType;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;

public class Metrics {
	public int totalAmountOfLines = 0;
	public int totalAmountOfEffectiveLines = 0;
	public int totalAmountOfNodes = 0;
	public int totalAmountOfTokens = 0;
	
	public int amountOfLinesCloned = 0;
	public int amountOfEffectiveLinesCloned = 0;
	public int amountOfNodesCloned = 0;
	public int amountOfTokensCloned = 0;
	
	public int overlappingNodes = 0;
	public int overlappingEffectiveLines = 0;
	public int overlappingTokens = 0;
	public int overlappingLines = 0;

	public final CountMap<RelationType> amountPerRelation = new CountMap<>();
	public final CountMap<LocationType> amountPerLocation = new CountMap<>();
	public final CountMap<ContentsType> amountPerContents = new CountMap<>();
	public final CountMap<Refactorability> amountPerExtract = new CountMap<>();
	
	public final CountMap<Integer> amountPerCloneClassSize = new CountMap<>();
	public final CountMap<Integer> amountPerNodes = new CountMap<>();
	public final CountMap<Integer> amountPerTotalNodeVolume = new CountMap<>();
	
	public final CountMap<Integer> amountPerEffectiveLines = new CountMap<>();
	public final CountMap<Integer> amountPerTotalEffectiveLineVolume = new CountMap<>();



	@Override
	public String toString() {
		return "Metrics [totalAmountOfLines=" + totalAmountOfLines + ", totalAmountOfEffectiveLines="
				+ totalAmountOfEffectiveLines + ", totalAmountOfNodes=" + totalAmountOfNodes + ", totalAmountOfTokens="
				+ totalAmountOfTokens + ", amountOfLinesCloned=" + amountOfLinesCloned
				+ ", amountOfEffectiveLinesCloned=" + amountOfEffectiveLinesCloned + ", amountOfNodesCloned="
				+ amountOfNodesCloned + ", amountOfTokensCloned=" + amountOfTokensCloned + ", overlappingNodes="
				+ overlappingNodes + ", overlappingEffectiveLines=" + overlappingEffectiveLines + ", overlappingTokens="
				+ overlappingTokens + ", overlappingLines=" + overlappingLines + ", amountPerRelation="
				+ amountPerRelation + ", amountPerLocation=" + amountPerLocation + ", amountPerContents="
				+ amountPerContents + ", amountPerExtract=" + amountPerExtract + ", amountPerCloneClassSize="
				+ amountPerCloneClassSize + ", amountPerNodes=" + amountPerNodes + ", amountPerTotalNodeVolume="
				+ amountPerTotalNodeVolume + ", amountPerEffectiveLines=" + amountPerEffectiveLines
				+ ", amountPerTotalEffectiveLineVolume=" + amountPerTotalEffectiveLineVolume + "]";
	}



	public void add(Metrics metrics) {
		totalAmountOfLines+=metrics.totalAmountOfLines;
		totalAmountOfNodes+=metrics.totalAmountOfNodes;
		totalAmountOfTokens+=metrics.totalAmountOfTokens;
		totalAmountOfEffectiveLines+=metrics.totalAmountOfEffectiveLines;
		
		amountOfLinesCloned+=metrics.amountOfLinesCloned;
		amountOfNodesCloned+=metrics.amountOfNodesCloned;
		amountOfTokensCloned+=metrics.amountOfTokensCloned;
		amountOfEffectiveLinesCloned+=metrics.amountOfEffectiveLinesCloned;
		
		overlappingNodes += metrics.overlappingNodes;
		overlappingEffectiveLines = metrics.overlappingEffectiveLines;
		overlappingTokens = metrics.overlappingTokens;
		overlappingLines = metrics.overlappingLines;
		
		amountPerRelation.addAll(metrics.amountPerRelation);
		amountPerLocation.addAll(metrics.amountPerLocation);
		amountPerContents.addAll(metrics.amountPerContents);
		amountPerExtract.addAll(metrics.amountPerExtract);
		
		amountPerCloneClassSize.addAll(metrics.amountPerCloneClassSize);
		amountPerNodes.addAll(metrics.amountPerNodes);
		amountPerTotalNodeVolume.addAll(metrics.amountPerTotalNodeVolume);
		
		amountPerEffectiveLines.addAll(metrics.amountPerEffectiveLines);
		amountPerTotalEffectiveLineVolume.addAll(metrics.amountPerTotalEffectiveLineVolume);
	}

	
	
	
}
