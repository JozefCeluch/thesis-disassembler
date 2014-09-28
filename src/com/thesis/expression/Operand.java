package com.thesis.expression;

public enum Operand {
	ADD("+"),
	SUBTRACT("-"),
	MULTIPLY("*"),
	DIVIDE("/"),
	REMAINDER("%"),
	BITWISE_OR("|"),
	BITWISE_AND("&"),
	BITWISE_XOR("^"),
	SHIFT_LEFT("<<"),
	SHIFT_RIGHT(">>")
	;

	private String mOperation;

	private Operand(String operation){
		mOperation = operation;
	}

	@Override
	public String toString() {
		return mOperation;
	}
}
