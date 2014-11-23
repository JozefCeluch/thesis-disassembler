package com.thesis.expression;

public class StackItem {

	public int labelId;
	public Expression expression;
	public int line;

	public StackItem(Expression expression, int label, int line) {
		labelId = label;
		this.expression = expression;
		this.line = line;
	}
}
