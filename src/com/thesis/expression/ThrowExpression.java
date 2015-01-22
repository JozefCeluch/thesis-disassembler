package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.tree.InsnNode;

import java.io.IOException;
import java.io.Writer;

public class ThrowExpression extends Expression {

	private Expression mExpression;

	public ThrowExpression(InsnNode node) {
		super(node);
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
