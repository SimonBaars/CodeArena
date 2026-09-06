package com.simonbaars.codearena.problem;

/**
 * Metric categories spawned as smell mobs. The original Forge four plus demo extras
 * (Type-2/3 clones, nesting depth, god class) that Map to distinct entity types.
 */
public enum ProblemType {
	DUPLICATION("Duplication", "code_spider"),
	UNITCOMPLEXITY("Unit Complexity", "code_zombie"),
	UNITINTERFACESIZE("Unit Interface Size", "code_skeleton"),
	UNITVOLUME("Unit Volume", "code_creeper"),
	TYPE2CLONE("Type-2 Clone", "code_cave_spider"),
	TYPE3CLONE("Type-3 Clone", "code_witch"),
	NESTINGDEPTH("Nesting Depth", "code_blaze"),
	GODCLASS("God Class", "code_enderman");

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

	/** Short label for scoreboard (sidebar row limit). */
	public String scoreboardLabel() {
		return switch (this) {
			case DUPLICATION -> "Duplication";
			case UNITCOMPLEXITY -> "Complexity";
			case UNITINTERFACESIZE -> "Interface";
			case UNITVOLUME -> "Volume";
			case TYPE2CLONE -> "Type-2";
			case TYPE3CLONE -> "Type-3";
			case NESTINGDEPTH -> "Nesting";
			case GODCLASS -> "GodClass";
		};
	}
}
