package com.simonbaars.codearena.javaparser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Materializes the bundled {@code sample-project/} sources under the game directory
 * so {@code /codearena scan} can parse real on-disk {@code .java} files without
 * requiring an external workspace checkout.
 * <p>
 * Fabric does not sandbox ordinary mod filesystem reads; we still scope scans to
 * the server game directory for safety.
 */
public final class SampleProject {
	private static final Logger LOGGER = LoggerFactory.getLogger("codearena");

	public static final String DIR_NAME = "codearena-sample";

	private static final List<String> RESOURCE_FILES = List.of(
			"com/example/OrderService.java",
			"com/example/OrderHelpers.java");

	private SampleProject() {}

	public static Path defaultDir(Path gameDir) {
		return gameDir.resolve(DIR_NAME).toAbsolutePath().normalize();
	}

	/**
	 * Ensure {@code gameDir/codearena-sample} exists with bundled sources.
	 * Existing files are left alone (idempotent for playtests).
	 */
	public static Path ensureOnDisk(Path gameDir) throws IOException {
		Path root = defaultDir(gameDir);
		Files.createDirectories(root);
		for (String relative : RESOURCE_FILES) {
			Path target = root.resolve(relative);
			if (Files.isRegularFile(target)) {
				continue;
			}
			Files.createDirectories(target.getParent());
			String resource = "/sample-project/" + relative;
			try (InputStream in = SampleProject.class.getResourceAsStream(resource)) {
				if (in == null) {
					LOGGER.warn("Missing bundled sample resource {}", resource);
					continue;
				}
				Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
			}
		}
		return root;
	}
}
