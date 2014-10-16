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
	ARITHMETIC_SHIFT_LEFT("<<"),
	ARITHMETIC_SHIFT_RIGHT(">>"),
	LOGICAL_SHIFT_RIGHT(">>>")

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
