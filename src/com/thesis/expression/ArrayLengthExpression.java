package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.tree.InsnNode;

import java.io.IOException;
import java.io.Writer;

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
