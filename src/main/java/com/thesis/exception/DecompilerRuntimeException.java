package com.thesis.exception;

/**
 * Exception for cases that should not happen
 */
public class DecompilerRuntimeException extends RuntimeException {
	public DecompilerRuntimeException(String message) {
		super(message);
	}
}
