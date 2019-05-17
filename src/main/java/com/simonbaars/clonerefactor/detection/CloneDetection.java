package com.simonbaars.clonerefactor.detection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.ast.interfaces.DeterminesNodeTokens;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.detection.interfaces.ChecksThresholds;
import com.simonbaars.clonerefactor.detection.interfaces.RemovesDuplicates;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class CloneDetection implements ChecksThresholds, RemovesDuplicates, DeterminesNodeTokens {
	final List<Sequence> clones = new ArrayList<>();

	public CloneDetection() {}

	public List<Sequence> findChains(Location lastLoc) {
		for(Sequence buildingChains = new Sequence(); lastLoc!=null; lastLoc = lastLoc.getPrevLine()) {
			Sequence newClones = collectClones(lastLoc);
			if(!buildingChains.getSequence().isEmpty() || newClones.size()>1)
				buildingChains = makeValid(lastLoc, buildingChains, newClones); //Because of the recent additions the current sequence may be invalidated
			if(buildingChains.size() == 1 || (lastLoc.getPrevLine()!=null && lastLoc.getPrevLine().getFile()!=lastLoc.getFile()))
				buildingChains.getSequence().clear();
		}
		return clones;
	}


	private Sequence makeValid(Location lastLoc, Sequence oldClones, Sequence newClones) {
		Map<Location /*oldClones*/, Location /*newClones*/> validChains = oldClones.getSequence().stream().distinct().filter(oldClone -> 
			newClones.getSequence().stream().anyMatch(newClone -> newClone.getFile() == oldClone.getFile() && oldClone.getPrevLine()!=null && oldClone.getFile() == oldClone.getPrevLine().getFile()
		    && newClone.getContents().getRange()!= null && newClone.getContents().getRange().equals(oldClone.getPrevLine().getContents().getRange()))).collect(Collectors.toMap(e -> e, e -> e.getPrevLine()));
		
		collectFinishedClones(oldClones, newClones, validChains);
		mergeLocationsOnBasisOfChains(newClones, validChains);
		
		if(lastLoc.getPrevLine()==null || lastLoc.getPrevLine().getFile()!=lastLoc.getFile()) {
			checkValidClones(newClones, newClones.getSequence());
		}
		
		return newClones;
	}

	private void collectFinishedClones(Sequence oldClones, Sequence newClones, Map<Location, Location> validChains) {
		if(validChains.size()!=oldClones.size() && !oldClones.getSequence().isEmpty()) {
			checkValidClones(oldClones, oldClones.getSequence().stream().filter(e -> !newClones.getSequence().contains(e.getPrevLine())).collect(Collectors.toList()));
		}
	}

	private void mergeLocationsOnBasisOfChains(Sequence newClones, Map<Location, Location> validChains) {
		for(Entry<Location, Location> validChain : validChains.entrySet()) {
			Location l = validChain.getValue().mergeWith(validChain.getKey());
			newClones.getSequence().set(newClones.getSequence().indexOf(validChain.getValue()), l);
			validChain.setValue(l);
		}
	}

	private void checkValidClones(Sequence oldClones, List<Location> endedClones) {
		ListMap<Integer /*Sequence size*/, Location /* Clones */> cloneList = new ListMap<>();
		endedClones.stream().forEach(e -> cloneList.addTo(e.getAmountOfNodes(), e));
		for(Entry<Integer, List<Location>> entry : cloneList.entrySet()) {
			int amountOfNodes = entry.getKey();
			List<Location> l = entry.getValue();
			addAllNonEndedLocations(oldClones, amountOfNodes, l);
			createClone(l);
			continue;
		}
	}

	private void createClone(List<Location> l) {
		Sequence newSequence = new Sequence(l);
		if(l.size()>1 && checkThresholds(newSequence)) {
			newSequence.isValid();
			if(removeDuplicatesOf(clones, newSequence))
				clones.add(newSequence);
		}
	}

	private void addAllNonEndedLocations(Sequence oldClones, int amountOfNodes, List<Location> l) {
		for(Location l2 : oldClones.getSequence()) {
			if(!l.contains(l2) && l2.getAmountOfNodes()>=amountOfNodes) {
				l.add(new Location(l2, getRange(l2, amountOfNodes)));
			}
		}
	}
	
	private Range getRange(Location l, int amountOfNodes) {
		return getRange(l.getContents().getNodes().get(0)).withEnd(getRange(l.getContents().getNodes().get(amountOfNodes-1)).end);
	}

	private Sequence collectClones(Location lastLoc) {
		Sequence c = new Sequence();
		while(lastLoc!=null) {
			c.add(lastLoc);
			lastLoc = lastLoc.getClone();
		}
		return c;
	}
}

