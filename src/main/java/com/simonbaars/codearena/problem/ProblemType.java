package com.simonbaars.codearena.problem;

/**
 * Port of {@code com.simonbaars.clonerefactor.metrics.ProblemType} — metric categories
 * that mapped to CodeSpider / CodeZombie / CodeSkeleton / CodeCreeper in the Forge mod.
 */
public enum ProblemType {
	DUPLICATION("Duplication", "spider"),
	UNITCOMPLEXITY("Unit Complexity", "zombie"),
	UNITINTERFACESIZE("Unit Interface Size", "skeleton"),
	UNITVOLUME("Unit Volume", "creeper");

	private final String displayName;
	private final String mobHint;

	ProblemType(String displayName, String mobHint) {
		this.displayName = displayName;
		this.mobHint = mobHint;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getMobHint() {
		return mobHint;
	}
}
