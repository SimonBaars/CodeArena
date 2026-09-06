package com.simonbaars.codearena.problem;

import java.util.List;

/**
 * Demo "detection results" used when a CloneRefactor jar/API is not on the classpath (real AST still requires that jar).
 * Eight problem types across three packages (enables diamond package-filter).
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
						"Extract duplicated validation into a shared helper; keep one source of truth."),
				new CodeProblem(
						ProblemType.DUPLICATION,
						"CartController.copyPasteAuth",
						"com.example.shop.cart",
						6,
						"Same auth preamble in multiple controllers — Extract Method + shared filter."),
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
						"Shrink the public interface (ISP): clients should not depend on unused methods."),
				new CodeProblem(
						ProblemType.UNITVOLUME,
						"ReportBuilder.buildAnnual",
						"com.example.shop.report",
						15,
						"Long method / high volume: extract steps (load → transform → format) into named units."),
				new CodeProblem(
						ProblemType.TYPE2CLONE,
						"PriceRules.tierA/tierB",
						"com.example.shop.pricing",
						10,
						"Type-2 clone (renamed identifiers). Parameterize the differing names into one method."),
				new CodeProblem(
						ProblemType.TYPE3CLONE,
						"InvoiceExport.csv/xml",
						"com.example.shop.report",
						11,
						"Type-3 clone (gapped statements). Form Template Method for shared export skeleton."),
				new CodeProblem(
						ProblemType.NESTINGDEPTH,
						"Fulfillment.shipIfReady",
						"com.example.shop.order",
						7,
						"Deep nesting: early returns / guard clauses flatten the pyramid of doom."),
				new CodeProblem(
						ProblemType.GODCLASS,
						"ShopManager",
						"com.example.shop",
						18,
						"God class: split by responsibility (orders, catalog, payments) into cohesive types.")
		);
	}
}
