package nl.sandersimon.clonedetection.common;

import java.io.File;

public class SavePaths {
	
	private static String dataFolder = ".clone";

	private SavePaths() {}
	
	public static void setAlternativeDataFolder(String folder) {
		dataFolder = folder;
	}
	
	public static String createDirectoryIfNotExists(String path){
		new File(path).mkdirs();
		return path;
	}

	public static String getApplicationDataFolder() {
		return getPathForOS(System.getProperty("os.name")) + File.separator + dataFolder + File.separator;
	}
	
	public static String getResourceFolder() {
		return getApplicationDataFolder() + "resources" + File.separator;
	}
	
	public static String getProjectFolder() {
		return getApplicationDataFolder() + "projects" + File.separator;
	}
	
	public static boolean resourceFolderExists() {
		return new File(getResourceFolder()).exists();
	}

	private static String getPathForOS(String os) {
		os = os.toLowerCase();
		if (os.contains("win")) return System.getenv("APPDATA");
		else if (os.contains("mac")) return System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support";
		else return System.getProperty("user.home");
	}
}