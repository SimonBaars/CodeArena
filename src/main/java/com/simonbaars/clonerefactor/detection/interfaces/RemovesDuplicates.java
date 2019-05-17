package com.simonbaars.clonerefactor.detection.interfaces;

import java.util.List;

import com.simonbaars.clonerefactor.model.Sequence;

public interface RemovesDuplicates {
	public default boolean removeDuplicatesOf(List<Sequence> clones, Sequence l) {
		l.getSequence().removeIf(e -> l.getSequence().stream().anyMatch(f -> f!=e && f.getFile() == e.getFile() && f.getRange().contains(e.getRange())));
		clones.removeIf(e -> isSubset(e, l));
		return !clones.stream().anyMatch(e -> isSubset(l, e));
	}
	
	public default boolean isSubset(Sequence existentClone, Sequence newClone) {
		return existentClone.getSequence().stream().allMatch(oldLoc -> newClone.getSequence().stream().anyMatch(newLoc -> oldLoc.getFile() == newLoc.getFile() && newLoc.getRange().contains(oldLoc.getRange())));
	}
}
