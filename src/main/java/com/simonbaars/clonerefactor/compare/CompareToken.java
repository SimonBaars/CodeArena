package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.JavaToken;
import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareToken extends Compare {
	
	private final JavaToken token;
	
	public CompareToken(CloneType cloneType, JavaToken token) {
		super(cloneType, token.getRange().get());
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && token.equals(((CompareToken)o).token);
	}

	@Override
	public int getHashCode() {
		return token.hashCode();
	}

	@Override
	public String toString() {
		return "CompareToken [token=" + token.asString() + "]";
	}
}
