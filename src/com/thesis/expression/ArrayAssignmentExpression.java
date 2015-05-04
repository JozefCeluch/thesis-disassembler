package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.expression.stack.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents an assignment of value to an array element, storing
 *<p>
 * used for the following instructions:
 * IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE
 *
 * before being pushed to the stack it pushes the accessed index and creates an {@link ArrayAccessExpression}that it stores only locally
 */
public class ArrayAssignmentExpression extends Expression {

	private ArrayAccessExpression mArray;
	private Expression mIndex;
	private Expression mValue;

	public ArrayAssignmentExpression(int opCode, Expression index, Expression value) {
		super(opCode);
		mIndex = index;
		mValue = value;
	}

	@Override
	public DataType getType() {
		return mArray.getType();
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		stack.push(mIndex, false);
		stack.push(new ArrayAccessExpression(mOpCode));
		mArray = (ArrayAccessExpression) stack.pop();
	}

	@Override
	public void write(Writer writer) throws IOException {
		mArray.write(writer);
		writer.write(" = ");
		mValue.write(writer);
	}
}
