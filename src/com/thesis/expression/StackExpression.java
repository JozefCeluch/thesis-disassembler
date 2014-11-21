package com.thesis.expression;

public class StackExpression {

	public int labelId;
	public Expression expression;
	public int line;

	public StackExpression(Expression expression, int label, int line) {
		labelId = label;
		this.expression = expression;
		this.line = line;
	}
}
