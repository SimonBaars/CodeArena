package nl.sandersimon.clonedetection.common;

public enum OperatingSystem {
	WINDOWS, LINUX, MACOS;
	
	public boolean isUnix() {
		return this != WINDOWS;
	}
}