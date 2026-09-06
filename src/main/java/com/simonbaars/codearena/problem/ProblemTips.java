package com.simonbaars.codearena.problem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Loads short tip blurbs from classpath {@code /tips/*.html} when present,
 * falling back to the tip embedded on each {@link CodeProblem}.
 */
public final class ProblemTips {
	private static final Map<ProblemType, String> CACHE = new EnumMap<>(ProblemType.class);

	private ProblemTips() {}

	public static String tipFor(CodeProblem problem) {
		String fromHtml = CACHE.computeIfAbsent(problem.getType(), ProblemTips::loadHtmlSnippet);
		if (fromHtml != null && !fromHtml.isBlank()) {
			return fromHtml;
		}
		return problem.getTip();
	}

	private static String loadHtmlSnippet(ProblemType type) {
		String resource = switch (type) {
			case DUPLICATION, TYPE2CLONE, TYPE3CLONE -> "/tips/duplication.html";
			case UNITVOLUME, GODCLASS -> "/tips/longmethod.html";
			case UNITINTERFACESIZE -> "/tips/unitinterface.html";
			default -> null;
		};
		if (resource == null) {
			return null;
		}
		try (InputStream in = ProblemTips.class.getResourceAsStream(resource)) {
			if (in == null) {
				return null;
			}
			String html = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
					.lines()
					.collect(Collectors.joining(" "));
			String plain = html
					.replaceAll("(?is)<script.*?</script>", " ")
					.replaceAll("(?is)<style.*?</style>", " ")
					.replaceAll("(?is)<[^>]+>", " ")
					.replaceAll("&nbsp;", " ")
					.replaceAll("&amp;", "&")
					.replaceAll("\\s+", " ")
					.trim();
			if (plain.length() > 220) {
				plain = plain.substring(0, 217) + "...";
			}
			return plain.isEmpty() ? null : plain;
		} catch (Exception e) {
			return null;
		}
	}
}
