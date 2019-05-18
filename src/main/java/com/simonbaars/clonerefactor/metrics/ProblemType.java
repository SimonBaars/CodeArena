package com.simonbaars.clonerefactor.metrics;

public enum ProblemType {
	DUPLICATION("Duplication"),
	UNITINTERFACESIZE("Unit Interface Size"),
	UNITCOMPLEXITY("Unit Complexity"),
	UNITVOLUME("Unit Volume");
	
	private final String name;
	
	private ProblemType(String name) {
		this.name= name;
	}

	public String getName() {
		return name;
	}
}
