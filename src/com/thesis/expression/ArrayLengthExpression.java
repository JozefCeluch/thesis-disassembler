package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents access to the field length of an array
 *<p>
 * used for the ARRAYLENGTH instruction
 *
 * before being pushed to the stack, it pops the expression from the top of the stack
 */
public class ArrayLengthExpression extends Expression {

	private Expression mExpression;

	public ArrayLengthExpression(int opCode) {
		super(opCode);
	}

	@Override
	public DataType getType() {
		return DataType.INT;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		mExpression = stack.pop();
	}

	@Override
	public void write(Writer writer) throws IOException {
		mExpression.write(writer);
		writer.write(".length");
	}
}
