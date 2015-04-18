package com.thesis.expression;

public abstract class JumpExpression extends ConditionalExpression {

	public JumpExpression(int opCode, int jumpLocation) {
		super(opCode, jumpLocation);
	}

}
