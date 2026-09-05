package com.simonbaars.codearena.problem;

import java.util.List;

/**
 * Demo "detection results" used when the full CloneRefactor AST pipeline is not on the classpath.
 * Mirrors the four ProblemType categories the Forge arena spawned as distinct mobs.
 */
public final class DemoProblems {
	private DemoProblems() {}

	public static List<CodeProblem> sampleWave() {
		return List.of(
				new CodeProblem(
						ProblemType.DUPLICATION,
						"OrderService.duplicateValidate",
						"com.example.shop.order",
						8,
						"Extract the duplicated validation into a shared helper; keep one source of truth."),
				new CodeProblem(
						ProblemType.UNITCOMPLEXITY,
						"PaymentGateway.process",
						"com.example.shop.payment",
						12,
						"Reduce cyclomatic complexity: split branches into smaller methods or a strategy map."),
				new CodeProblem(
						ProblemType.UNITINTERFACESIZE,
						"CatalogFacade",
						"com.example.shop.catalog",
						9,
						"Shrink the public interface: ISP — clients should not depend on unused methods."),
				new CodeProblem(
						ProblemType.UNITVOLUME,
						"ReportBuilder.buildAnnual",
						"com.example.shop.report",
						15,
						"Long method / high volume: extract steps (load → transform → format) into named units.")
		);
	}
}
