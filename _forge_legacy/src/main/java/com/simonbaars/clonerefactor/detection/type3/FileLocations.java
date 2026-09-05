package com.simonbaars.clonerefactor.detection.type3;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class FileLocations {
	private final Sequence seq;
	
	public FileLocations (Sequence seq) {
		Collections.sort(seq.getSequence());
		this.seq = seq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
        for (Location e : seq.getSequence()) 
        	result = prime*result + (e==null ? 0 : e.getFile().hashCode());
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
		FileLocations other = (FileLocations) obj;
		return seq.getSequence().size() == seq.getSequence().size() && IntStream.range(0, seq.getSequence().size()).allMatch(i -> seq.getSequence().get(i).getFile().equals(other.seq.getSequence().get(i).getFile()));
	}

	public List<Location> getLocs() {
		return seq.getSequence();
	}

	public Sequence getSeq() {
		return seq;
	}
	
	
}
