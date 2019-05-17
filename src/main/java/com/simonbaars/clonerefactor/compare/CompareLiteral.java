package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.JavaToken;
import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareLiteral extends Compare {
	final JavaToken t;
	
	public CompareLiteral(CloneType cloneType, JavaToken t) {
		super(cloneType, t.getRange().get());
		this.t=t;
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && (cloneType.isNotTypeOne() || t.equals(((CompareLiteral)o).t)); 
	}

	@Override
	public int getHashCode() {
		return cloneType.isNotTypeOne() ? -1 : t.hashCode();
	}

	@Override
	public String toString() {
		return "CompareLiteral [t=" + t + "]";
	}
}
