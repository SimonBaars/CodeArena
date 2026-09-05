package com.simonbaars.codearena.problem;

/**
 * Simplified stand-in for legacy {@code MetricProblem} without JavaParser / Sequence.
 * Carries enough metadata for naming, scaling, scoring, and chat tips.
 */
public final class CodeProblem {
	private final ProblemType type;
	private final String name;
	private final String packageName;
	private final int severity;
	private final String tip;

	public CodeProblem(ProblemType type, String name, String packageName, int severity, String tip) {
		this.type = type;
		this.name = name;
		this.packageName = packageName;
		this.severity = Math.max(1, severity);
		this.tip = tip;
	}

	public ProblemType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getPackageName() {
		return packageName;
	}

	public int getSeverity() {
		return severity;
	}

	public String getTip() {
		return tip;
	}

	public String scoreLabel() {
		return type.getDisplayName();
	}

	public String chatSummary() {
		return type.getDisplayName() + " · " + name + " (" + packageName + ") severity " + severity;
	}
}
