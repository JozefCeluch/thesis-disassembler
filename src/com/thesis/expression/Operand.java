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
	LOGICAL_SHIFT_RIGHT(">>>"),
	EQUAL("==", "!="),
	NOT_EQUAL("!=", "=="),
	GREATER_EQUAL(">=", "<"),
	LESS_EQUAL("<=", ">"),
	GREATER_THAN(">", "<="),
	LESS_THAN("<", ">="),
	ERR("ERR")
	;

	private String mOperation;
	private String mOpposite;

	private Operand(String operation){
		mOperation = operation;
		mOpposite = null;
	}

	private Operand(String operation, String opposite){
		mOperation = operation;
		mOpposite = opposite;
	}

	@Override
	public String toString() {
		return mOperation;
	}

	public Operand neg() {
		if (mOpposite != null) {
			return getOperand(this.mOpposite);
		}
		return this;
	}

	private Operand getOperand(String op){
		switch (op){
			case "==":
				return EQUAL;
			case "!=":
				return NOT_EQUAL;
			case "<=":
				return LESS_EQUAL;
			case ">=":
				return GREATER_EQUAL;
			case ">":
				return GREATER_THAN;
			case "<":
				return LESS_THAN;
		}
		return ERR;
	}
}
