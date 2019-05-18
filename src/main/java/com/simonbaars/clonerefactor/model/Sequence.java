package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.model.location.Location;

public class Sequence implements Comparable<Sequence> {
	final List<Location> sequence;
	
	private RelationType relationType;
	private Refactorability refactorability;

	public Sequence(List<Location> collection) {
		super();
		this.sequence = collection;
	}

	public Sequence() {
		super();
		this.sequence = new ArrayList<>();
	}

	public Sequence(Collection<Location> values) {
		this(new ArrayList<>(values));
	}

	public Sequence(Sequence copy, int begin, int end) {
		this.sequence = copy.sequence.subList(begin, end);
	}

	public List<Location> getSequence() {
		return sequence;
	}
	
	public Sequence add(Location l) {
		sequence.add(l);
		return this; //For method chaining
	}

	public int size() {
		return sequence.size();
	}

	@Override
	public String toString() {
		return "Sequence [sequence=" + Arrays.toString(sequence.toArray()) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sequence other = (Sequence) obj;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		return true;
	}

	public int getNodeSize() {
		return sequence.isEmpty() ? 0 : sequence.get(0).getAmountOfNodes();
	}
	
	public int getEffectiveLineSize() {
		return sequence.isEmpty() ? 0 : sequence.get(0).getEffectiveLines();
	}
	
	public int getTotalNodeVolume() {
		return sequence.stream().mapToInt(e -> e.getAmountOfNodes()).sum();
	}
	
	public int getTotalTokenVolume() {
		return sequence.stream().mapToInt(e -> e.getAmountOfTokens()).sum();
	}
	
	public int getTotalEffectiveLineVolume() {
		return sequence.stream().mapToInt(e -> e.getEffectiveLines()).sum();
	}

	public Location getAny() {
		return sequence.get(0);
	}

	@Override
	public int compareTo(Sequence o) {
		if(getTotalNodeVolume() == o.getTotalNodeVolume())
			return Integer.compare(o.getTotalTokenVolume(), getTotalTokenVolume());
		return Integer.compare(o.getTotalNodeVolume(), getTotalNodeVolume());
	}
	
	public void setMetrics(CloneRelation relation, CloneRefactorability r) {
		relationType = relation.get(this);
		refactorability = r.get(this);
	}
	
	public RelationType getRelationType() {
		return relationType;
	}
	
	public Refactorability getRefactorability() {
		return refactorability;
	}
	
	public void isValid() {
		if(sequence.stream().map(e -> e.getContents().getNodes().size()).distinct().count()>1)
			throw new IllegalStateException("Invalid Sequence "+this);
	}
}
