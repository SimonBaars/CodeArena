package com.simonbaars.clonerefactor.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SavePaths {
	
	private static String dataFolder = "clone";
	private static String timestamp = null;

	private SavePaths() {}
	
	public static void genTimestamp() {
		timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	public static void setAlternativeDataFolder(String folder) {
		dataFolder = folder;
	}
	
	public static String createDirectoryIfNotExists(String path){
		new File(path).mkdirs();
		return path;
	}

	public static String getApplicationDataFolder() {
		return getPathForOS() + File.separator + dataFolder + File.separator;
	}
	
	public static String getOutputFolder() {
		return getApplicationDataFolder() + "output" + File.separator;
	}

	private static String getPathForOS() {
		switch(OperatingSystem.get()) {
			case WINDOWS: return System.getenv("APPDATA");
			default: return System.getProperty("user.home");
		}
	}

	public static String getFullOutputFolder() {
		return getMyOutputFolder()+"full"+File.separator;
	}
	
	public static String getMyOutputFolder() {
		if(timestamp == null)
			genTimestamp();
		return SavePaths.createDirectoryIfNotExists(SavePaths.getOutputFolder())+timestamp+File.separator;
	}

	public static String getJavaProjectFolder() {
		return getApplicationDataFolder()+"java_projects"+File.separator;
	}

	public static String getGitFolder() {
		return getApplicationDataFolder()+"git"+File.separator;
	}
	
	public static String getGitSourcesFolder() {
		return getApplicationDataFolder()+"gitsrc"+File.separator;
	}

	public static String getErrorFolder() {
		return getMyOutputFolder()+"errors"+File.separator;
	}
}