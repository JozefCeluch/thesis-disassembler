package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents the throw keyword
 * <p>
 * used for the ATHROW instruction
 */
public class ThrowExpression extends Expression {

	private Expression mExpression;

	public ThrowExpression(int opCode) {
		super(opCode);
	}

	@Override
	public DataType getType() {
		return null;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		mExpression = stack.pop();
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("throw ");
		mExpression.write(writer);
	}
}
