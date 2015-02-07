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
