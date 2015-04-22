package com.thesis.expression.stack;

import com.thesis.expression.Expression;

public class StackItem {

	private final int labelId;
	private Expression expression;
	private final int line;

	public StackItem(Expression expression, int label, int line) {
		labelId = label;
		this.expression = expression;
		this.line = line;
	}

	public int getLabelId() {
		return labelId;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public int getLine() {
		return line;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StackItem stackItem = (StackItem) o;

		if (labelId != stackItem.labelId) return false;
		if (line != stackItem.line) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = labelId;
		result = 31 * result + line;
		return result;
	}
}
