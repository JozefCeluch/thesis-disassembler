package com.thesis.expression;

public enum LogicGateOperand {
	AND("&&"),
	OR("||");

	private final String mOperand;

	private LogicGateOperand(String operand){
		mOperand = operand;
	}

	@Override
	public String toString() {
		return mOperand;
	}
}
