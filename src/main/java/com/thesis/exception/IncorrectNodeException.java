package com.thesis.exception;

/**
 * Exceptions for cases when the {@link com.thesis.translator.handler.NodeHandler} encounters an unexpected node type
 */
public class IncorrectNodeException extends DecompilerRuntimeException {
	public IncorrectNodeException(String message) {
		super(message);
	}
}
