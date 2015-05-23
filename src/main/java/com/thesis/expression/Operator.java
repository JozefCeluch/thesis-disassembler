package com.thesis.expression;

/**
 * Arithmetic operation
 */
public enum Operator {
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
	AND("&&", "||"),
	OR("||", "&&"),
	ERR("ERR")
	;

	private String mOperation;
	private String mOpposite;

	Operator(String operation){
		mOperation = operation;
		mOpposite = operation;
	}

	Operator(String operation, String opposite){
		mOperation = operation;
		mOpposite = opposite;
	}

	@Override
	public String toString() {
		return mOperation;
	}

	public Operator neg() {
		return getOpposite();
	}

	private Operator getOpposite(){
		String op = mOpposite;
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
			case "||":
				return OR;
			case "&&":
				return AND;
		}
		return this;
	}
}
