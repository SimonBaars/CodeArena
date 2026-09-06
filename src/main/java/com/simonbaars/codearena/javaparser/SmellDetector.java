package com.simonbaars.codearena.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.simonbaars.codearena.problem.CodeProblem;
import com.simonbaars.codearena.problem.ProblemType;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thin JavaParser-based smell detector for Fabric CodeArena.
 * Detects method-level duplication / complexity / volume / parameter-count smells
 * on embedded demo sources (or any in-memory Java source map).
 * <p>
 * This is <strong>not</strong> full CloneRefactor Type-2/3 clone detection —
 * only identical normalized method bodies and simple complexity heuristics.
 */
public final class SmellDetector {
	private static final Logger LOGGER = LoggerFactory.getLogger("codearena");

	private static final String[] DEMO_RESOURCES = {
			"/demo-sources/DemoSmells.java",
			"/demo-sources/DuplicateCode.java"
	};

	private static final int COMPLEXITY_THRESHOLD = 10;
	private static final int VOLUME_THRESHOLD = 50;
	private static final int PARAM_THRESHOLD = 5;
	private static final int MIN_DUP_BODY_CHARS = 50;

	private final JavaParser parser;

	public SmellDetector() {
		ParserConfiguration config = new ParserConfiguration();
		config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
		this.parser = new JavaParser(config);
	}

	/**
	 * Scan classpath-embedded demo sources. Returns an empty list on total failure
	 * so callers can fall back to {@code DemoProblems.sampleWave()}.
	 */
	public List<CodeProblem> detectDemoWave() {
		Map<String, String> sources = new HashMap<>();
		for (String resource : DEMO_RESOURCES) {
			try (InputStream in = SmellDetector.class.getResourceAsStream(resource)) {
				if (in == null) {
					LOGGER.warn("Missing demo source resource {}", resource);
					continue;
				}
				String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
				String name = resource.substring(resource.lastIndexOf('/') + 1);
				sources.put(name, content);
			} catch (Exception e) {
				LOGGER.warn("Failed to read demo source {}: {}", resource, e.toString());
			}
		}
		if (sources.isEmpty()) {
			return List.of();
		}
		return detectFromSources(sources);
	}

	/**
	 * Parse and detect smells from a map of logical path → Java source text.
	 */
	public List<CodeProblem> detectFromSources(Map<String, String> pathToSource) {
		List<CodeProblem> problems = new ArrayList<>();
		List<ParsedUnit> units = new ArrayList<>();

		for (Map.Entry<String, String> entry : pathToSource.entrySet()) {
			try {
				ParseResult<CompilationUnit> result = parser.parse(entry.getValue());
				if (result.isSuccessful() && result.getResult().isPresent()) {
					units.add(new ParsedUnit(entry.getKey(), result.getResult().get()));
				} else {
					LOGGER.warn("Parse failed for {}: {}", entry.getKey(), result.getProblems());
				}
			} catch (Exception e) {
				LOGGER.warn("Parse exception for {}: {}", entry.getKey(), e.toString());
			}
		}

		if (units.isEmpty()) {
			return List.of();
		}

		problems.addAll(detectDuplication(units));
		problems.addAll(detectComplexity(units));
		problems.addAll(detectLargeUnits(units));
		problems.addAll(detectLargeInterfaces(units));
		return List.copyOf(problems);
	}

	private List<CodeProblem> detectDuplication(List<ParsedUnit> units) {
		Map<String, List<MethodHit>> byBody = new HashMap<>();
		for (ParsedUnit unit : units) {
			String pkg = packageOf(unit.cu);
			unit.cu.findAll(MethodDeclaration.class).forEach(method -> {
				Optional<BlockStmt> body = method.getBody();
				if (body.isEmpty()) {
					return;
				}
				String normalized = normalizeCode(body.get().toString());
				if (normalized.length() <= MIN_DUP_BODY_CHARS) {
					return;
				}
				byBody.computeIfAbsent(normalized, k -> new ArrayList<>())
						.add(new MethodHit(unit.path, pkg, classNameOf(method), method));
			});
		}

		List<CodeProblem> out = new ArrayList<>();
		for (List<MethodHit> group : byBody.values()) {
			if (group.size() < 2) {
				continue;
			}
			int totalLines = 0;
			List<String> names = new ArrayList<>();
			for (MethodHit hit : group) {
				Optional<Range> range = hit.method.getRange();
				if (range.isPresent()) {
					totalLines += range.get().end.line - range.get().begin.line + 1;
				}
				names.add(hit.className + "." + hit.method.getNameAsString());
			}
			int avgLines = Math.max(1, totalLines / group.size());
			String tip = "Extract duplicated method bodies into a shared helper; keep one source of truth. "
					+ "(Method-level identical-body detection — not Type-2/3 CloneRefactor.)";
			out.add(new CodeProblem(
					ProblemType.DUPLICATION,
					String.join(" / ", names),
					group.get(0).pkg,
					clampSeverity(avgLines),
					tip));
		}
		return out;
	}

	private List<CodeProblem> detectComplexity(List<ParsedUnit> units) {
		List<CodeProblem> out = new ArrayList<>();
		for (ParsedUnit unit : units) {
			String pkg = packageOf(unit.cu);
			unit.cu.findAll(MethodDeclaration.class).forEach(method -> {
				int complexity = calculateCyclomaticComplexity(method);
				if (complexity <= COMPLEXITY_THRESHOLD) {
					return;
				}
				out.add(new CodeProblem(
						ProblemType.UNITCOMPLEXITY,
						classNameOf(method) + "." + method.getNameAsString(),
						pkg,
						clampSeverity(complexity),
						"Reduce cyclomatic complexity: split branches into smaller methods or a strategy map."));
			});
		}
		return out;
	}

	private List<CodeProblem> detectLargeUnits(List<ParsedUnit> units) {
		List<CodeProblem> out = new ArrayList<>();
		for (ParsedUnit unit : units) {
			String pkg = packageOf(unit.cu);
			unit.cu.findAll(MethodDeclaration.class).forEach(method -> {
				Optional<Range> range = method.getRange();
				if (range.isEmpty()) {
					return;
				}
				int lines = range.get().end.line - range.get().begin.line + 1;
				if (lines <= VOLUME_THRESHOLD) {
					return;
				}
				out.add(new CodeProblem(
						ProblemType.UNITVOLUME,
						classNameOf(method) + "." + method.getNameAsString(),
						pkg,
						clampSeverity(lines / 4),
						"Long method / high volume: extract steps into named units."));
			});
		}
		return out;
	}

	private List<CodeProblem> detectLargeInterfaces(List<ParsedUnit> units) {
		List<CodeProblem> out = new ArrayList<>();
		for (ParsedUnit unit : units) {
			String pkg = packageOf(unit.cu);
			unit.cu.findAll(MethodDeclaration.class).forEach(method -> {
				int params = method.getParameters().size();
				if (params <= PARAM_THRESHOLD) {
					return;
				}
				out.add(new CodeProblem(
						ProblemType.UNITINTERFACESIZE,
						classNameOf(method) + "." + method.getNameAsString(),
						pkg,
						clampSeverity(params),
						"Shrink the parameter list (Introduce Parameter Object / builder)."));
			});
		}
		return out;
	}

	private static String packageOf(CompilationUnit cu) {
		return cu.getPackageDeclaration()
				.map(p -> p.getNameAsString())
				.orElse("default");
	}

	private static String classNameOf(MethodDeclaration method) {
		return method.findAncestor(ClassOrInterfaceDeclaration.class)
				.map(ClassOrInterfaceDeclaration::getNameAsString)
				.orElse("Unknown");
	}

	private static String normalizeCode(String code) {
		return code.replaceAll("/\\*.*?\\*/", "")
				.replaceAll("//.*", "")
				.replaceAll("\\s+", " ")
				.trim();
	}

	private static int calculateCyclomaticComplexity(MethodDeclaration method) {
		int complexity = 1;
		complexity += method.findAll(com.github.javaparser.ast.stmt.IfStmt.class).size();
		complexity += method.findAll(com.github.javaparser.ast.stmt.ForStmt.class).size();
		complexity += method.findAll(com.github.javaparser.ast.stmt.ForEachStmt.class).size();
		complexity += method.findAll(com.github.javaparser.ast.stmt.WhileStmt.class).size();
		complexity += method.findAll(com.github.javaparser.ast.stmt.DoStmt.class).size();
		complexity += method.findAll(com.github.javaparser.ast.stmt.SwitchEntry.class).size();
		complexity += method.findAll(com.github.javaparser.ast.stmt.CatchClause.class).size();
		complexity += method.findAll(com.github.javaparser.ast.expr.ConditionalExpr.class).size();
		return complexity;
	}

	private static int clampSeverity(int raw) {
		return Math.max(1, Math.min(20, raw));
	}

	/** Convenience used by ArenaSession: AST wave or empty. */
	public static List<CodeProblem> tryDetectDemoWave() {
		try {
			List<CodeProblem> found = new SmellDetector().detectDemoWave();
			if (!found.isEmpty()) {
				LOGGER.info("SmellDetector found {} method-level smells on demo sources: {}",
						found.size(),
						found.stream().map(CodeProblem::chatSummary).collect(Collectors.joining("; ")));
			}
			return found;
		} catch (Throwable t) {
			LOGGER.warn("SmellDetector failed; caller should fall back to DemoProblems: {}", t.toString());
			return List.of();
		}
	}

	private record ParsedUnit(String path, CompilationUnit cu) {}

	private record MethodHit(String path, String pkg, String className, MethodDeclaration method) {}
}
