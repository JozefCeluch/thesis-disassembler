package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents access to elements of an array
 *<p>
 * used for the following instructions:
 * IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD
 *
 * it is also used in {@link ArrayAssignmentExpression} since before assigning a value
 * to the array item, the item must be loaded first
 *
 * before being pushed to the stack it pops index that is accessed and the array reference
 */
public class ArrayAccessExpression extends Expression {

	private Expression indexExpression;
	private Expression arrayRef;

	public ArrayAccessExpression(int opCode) {
		super(opCode);
	}

	@Override
	public DataType getType() {
		return arrayRef.getType();
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		indexExpression = stack.pop();
		arrayRef = stack.pop();
	}

	@Override
	public void write(Writer writer) throws IOException {
		arrayRef.write(writer);
		writer.write("[");
		indexExpression.write(writer);
		writer.write("]");
	}
}
