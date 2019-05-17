package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.JavaToken;
import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareName extends Compare {
	JavaToken t;
	
	public CompareName(CloneType type, JavaToken t) {
		super(type, t.getRange().get());
		this.t = t;  
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && (cloneType.isNotTypeOne() || t.equals(((CompareName)o).t)); //Type two names will always be flagged as equals, as we don't take them into account.
	}

	@Override
	public int getHashCode() {
		return cloneType.isNotTypeOne() ? -2 : t.hashCode();
	}

	@Override
	public String toString() {
		return "CompareName [t=" + t + "]";
	}
}
