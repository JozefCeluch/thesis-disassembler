package com.thesis.exception;

/**
 * General decompilation exception
 */
public class DecompilerException extends Exception {

	public DecompilerException(String message) {
		super(message);
	}

	public DecompilerException(String message, Throwable cause) {
		super(message, cause);
	}
}
