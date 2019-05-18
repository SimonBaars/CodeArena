package com.simonbaars.clonerefactor.compare;

import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareFalse extends Compare {
	
	private static int x = Integer.MIN_VALUE;
	
	public CompareFalse() {
		super(CloneType.TYPE1, null);
	}

	@Override
	public boolean equals(Object o) {
		return false; // Whatever clones we'll find, I won't be a part of it.
	}
	
	@Override
	public int getHashCode() {
		return x++;
	}

	@Override
	public String toString() {
		return "CompareFalse";
	}
}
