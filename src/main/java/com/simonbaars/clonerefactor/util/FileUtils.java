package com.simonbaars.clonerefactor.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The FileUtils class provides all kinds of useful methods when testing with Selenium WebDriver.
 *
 */
public class FileUtils {

	private FileUtils() {
	}

	public static String getFileAsString(File file) throws IOException {
		return new String(getFileBytes(file), StandardCharsets.UTF_8);
	}

	public static byte[] getFileBytes(File file) throws IOException {
		return Files.readAllBytes(file.toPath());
	}
	
	/**
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	public static void writeStringToFile(File file, String content) throws IOException {
		if (file.exists())
			Files.delete(file.toPath());
		else if (file.getParentFile() != null)
			file.getParentFile().mkdirs();
		if (file.createNewFile())
			Files.write(Paths.get(file.getAbsolutePath()), content.getBytes(StandardCharsets.UTF_8));
	}

	public static Set<String> getVariables(String checkString, String delimiterBegin, String delimiterEnd) {
		Set<String> vars = new LinkedHashSet<>();
		int beginIndex;
		while ((beginIndex = checkString.indexOf(delimiterBegin)) != -1) {
			while (checkString.charAt(beginIndex + delimiterBegin.length()) == delimiterBegin.charAt(0)) {
				beginIndex++;
			}
			int endIndex = checkString.indexOf(delimiterEnd);
			String variableName = checkString.substring(beginIndex + delimiterBegin.length(), endIndex);
			vars.add(variableName);
			checkString = checkString.substring(endIndex + delimiterEnd.length());
		}
		return vars;
	}

	public static File getFile(FileSystem fs, String filePath) throws IOException {
		Path targetPath = new File(new File(filePath).getName()).toPath();
		Files.copy(fs.getPath(filePath), targetPath, StandardCopyOption.REPLACE_EXISTING);
		File file = new File(targetPath.toString());
		file.deleteOnExit();
		return file;
	}
	
	public static String getNumber(String text, char...including) {
		return getNumber(text, 0, including);
	}

	public static String getNumber(String text, int index, char...including) {
		StringBuilder floatNumber = new StringBuilder();
		String includingCharacters = new String(including);
		int matchesFound = 0;
		for(int i = 0; i<text.length(); i++) {
			char charAt = text.charAt(i);
			if(Character.isDigit(charAt) || includingCharacters.indexOf(charAt)!=-1) {
				floatNumber.append(charAt);
			} else if(floatNumber.length()!=0) {
				if(floatNumber.toString().matches(".*\\d+.*") && index == matchesFound) break;
				floatNumber.setLength(0);
				matchesFound++;
			}
		}
		return floatNumber.toString();
	}

	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static String numberToString(int number) {
		switch(number) {
			case 1: return number+"st";
			case 2: return number+"nd";
			case 3: return number+"rd";
			default: return number+"th";
		}
	}

	public static String getFileAsString(String string) throws IOException {
		return getFileAsString(new File(string));
	}
}