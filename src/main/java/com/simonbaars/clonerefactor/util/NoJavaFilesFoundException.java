package com.simonbaars.clonerefactor.util;

public class NoJavaFilesFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2972109211543532640L;

	public NoJavaFilesFoundException() {
		super("No Java files were found in this folder!");
	}

	public NoJavaFilesFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoJavaFilesFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoJavaFilesFoundException(String message) {
		super(message);
	}

	public NoJavaFilesFoundException(Throwable cause) {
		super(cause);
	}

	
}
