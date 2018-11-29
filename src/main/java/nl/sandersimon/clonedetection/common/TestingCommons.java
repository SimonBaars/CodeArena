package nl.sandersimon.clonedetection.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The TestingCommons class provides all kinds of useful methods when testing with Selenium WebDriver.
 *
 */
public class TestingCommons {

	private TestingCommons() {
	}

	public static String getFileAsString(File file) throws IOException {
		return new String(getFileBytes(file), StandardCharsets.UTF_8);
	}

	public static byte[] getFileBytes(File file) throws IOException {
		return Files.readAllBytes(file.toPath());
	}
	
	public static String getResourceAsString(String resourcePath) throws IOException {
		return getFileAsString(ResourceCommons.getResource(resourcePath));
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

	/**
	 * Returns the bytes contained in the given file.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static byte[] loadFile(File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			long length = file.length();
			if (length > Integer.MAX_VALUE) {
				throw new IOException("The input file is too large: " + file.getName());
			}
			byte[] bytes = new byte[(int) length];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			if (offset < bytes.length) {
				throw new IOException("Could not completely read file " + file.getName());
			}
			return bytes;
		}
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

	public static File getResourceFromFramework(String path) throws IOException {
		return getResourceSafe(path, TestingCommons.class);
	}
	
	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static File getResourceSafe(String path, @SuppressWarnings("rawtypes") Class protectionDomainProvider)
			throws IOException {
		final File jarFile = new File(
				protectionDomainProvider.getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) { // Run with JAR file
			Map<String, String> env = new HashMap<>();
			URI uri = URI.create("jar:" + jarFile.toPath().toUri());
			try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
				return getFile(fs, path);
			}
		} else { // Run with IDE
			final URL url = protectionDomainProvider.getResource("/" + path);
			if (url != null) {
				try {
					return new File(url.toURI());
				} catch (URISyntaxException ex) {
					Logger.getAnonymousLogger().log(Level.SEVERE, "URL not successfully converted!", ex);
				} // never happens
			} else {
				Logger.getAnonymousLogger().log(Level.WARNING, path+" does not exist!");
			}
		}
		return null;
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

	public static File getResourceFile(String resourcePath) {
		return ResourceCommons.getResource(resourcePath);
	}
	
	public static String numberToString(int number) {
		switch(number) {
			case 1: return number+"st";
			case 2: return number+"nd";
			case 3: return number+"rd";
			default: return number+"th";
		}
	}
	
	public static String getOSString() {
		switch(getOS()) {
			case WINDOWS: return "windows";
			case MACOS: return "osx";
			case LINUX: return "linux";
			default: return null;
		}		
	}
	
	public static OperatingSystem getOS() {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.startsWith("win"))
			return OperatingSystem.WINDOWS;
		else if(os.startsWith("mac"))
			return OperatingSystem.MACOS;
		return OperatingSystem.LINUX;
	}
}