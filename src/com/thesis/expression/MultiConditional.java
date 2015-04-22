package com.thesis.expression;

import com.thesis.expression.stack.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

public class MultiConditional extends ConditionalExpression {

	private Expression left;
	private Expression right;

	public MultiConditional(int opCode, int jumpDestination, Expression rightExpression , Expression leftExpression, ExpressionStack then) {
		super(opCode, jumpDestination);
		super.thenBranch = then;
		left = leftExpression;
		right = rightExpression;
	}

	public MultiConditional(int opCode, int jumpDestination, Expression rightExpression , Expression leftExpression) {
		super(opCode, jumpDestination);
		left = leftExpression;
		right = rightExpression;
	}

	@Override
	public void write(Writer writer) throws IOException {
		left.write(writer);
		writer.append(" ").append(mOperand.toString()).append(" ");
		right.write(writer);
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
	}
}
