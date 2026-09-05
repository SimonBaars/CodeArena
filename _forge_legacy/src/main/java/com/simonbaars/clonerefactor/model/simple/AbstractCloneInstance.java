package com.simonbaars.clonerefactor.model.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.simonbaars.clonerefactor.model.location.Location;

public class AbstractCloneInstance {
	final List<AbstractClonedStatement> acs = new ArrayList<>();
	
	public AbstractCloneInstance(Location l) {
		l.getContents().getNodes().forEach(n -> acs.add(new AbstractClonedStatement(n)));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acs == null) ? 0 : acs.hashCode());
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
		AbstractCloneInstance other = (AbstractCloneInstance) obj;
		if (acs == null) {
			if (other.acs != null)
				return false;
		} else if (!acs.equals(other.acs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AbstractCloneInstance [acs=" + Arrays.toString(acs.toArray()) + "]";
	}
}
