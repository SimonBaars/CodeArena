package com.simonbaars.clonerefactor.scripts.prepare;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.simonbaars.clonerefactor.util.SavePaths;

public class PrepareProjectsFolder {
	
	private static final File JAVA_PROJECTS_CORPUS_FOLDER = new File(SavePaths.getJavaProjectFolder());

	public static void main(String[] args) {
		System.out.println(JAVA_PROJECTS_CORPUS_FOLDER.listFiles(f -> isQualified(getSourceFolder(f), 5, 1000))[2888]);
	}
	
	public static File[] getFilteredCorpusFiles(int min, int max) {
		return JAVA_PROJECTS_CORPUS_FOLDER.listFiles(f -> isQualified(getSourceFolder(f), min, max));
	}

	public static File getSourceFolder(File f) {
		return new File(f.getAbsolutePath()+"/src/main/java");
	}
	
	public static boolean isQualified(File project, int min, int max) {
		return project.exists() && between(min, max, countJavaFiles(project));
	}

	private static boolean between(int min, int max, long javaFilesInProject) {
		return javaFilesInProject >=min && javaFilesInProject <= max;
	}

	private static long countJavaFiles(File project) {
			return getJavaFileStream(project).count();
	}

	private static Stream<Path> getJavaFileStream(File project) {
		try {
			return Files.walk(project.toPath())
		        .parallel()
		        .filter(p -> !p.toFile().isDirectory() && p.toString().endsWith(".java"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<File> getJavaFiles(File project){
		return getJavaFileStream(project).map(e -> e.toFile()).collect(Collectors.toList());
	}
	
}
