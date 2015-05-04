package com.thesis.expression;

import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents a comparison of a single value with zero
 * <p>
 * used for the following instructions:
 * IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE
 */
public class SingleConditional extends JumpExpression {

	private Expression mLeftExpression;

	public SingleConditional(int opCode, int jumpDestination, Expression leftExpression) {
		super(opCode, jumpDestination);
		mLeftExpression = leftExpression;
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (mOperand.equals(Operand.EQUAL)) {
			writer.write("!");
		}
		mLeftExpression.write(writer);
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
	}
}
