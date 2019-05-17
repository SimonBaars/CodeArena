package com.simonbaars.clonerefactor.settings;

public enum CloneType {
	TYPE1,TYPE2,TYPE3;

	public boolean isNotTypeOne() {
		return this!=TYPE1;
	}
}
