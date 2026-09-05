package com.simonbaars.codearena.common;

public enum OperatingSystem {
	WINDOWS, LINUX, MACOS;
	
	public boolean isUnix() {
		return this != WINDOWS;
	}
}