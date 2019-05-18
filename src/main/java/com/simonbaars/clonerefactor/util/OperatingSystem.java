package com.simonbaars.clonerefactor.util;

public enum OperatingSystem {
	WINDOWS, LINUX, MACOS;
	
	public boolean isUnix() {
		return this != WINDOWS;
	}
	
	public static OperatingSystem get() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) return WINDOWS;
		else if (os.contains("mac")) return MACOS;
		else return LINUX;
	}
}