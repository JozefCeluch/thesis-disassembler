package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class SingleConditional extends ConditionalExpression {

	private Expression mLeftExpression;

	public SingleConditional(AbstractInsnNode instruction, int jumpDestination, Expression leftExpression, ExpressionStack then) {
		super(instruction, jumpDestination);
		super.thenBranch = then;
		mLeftExpression = leftExpression;
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeftExpression.write(writer);
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
	}
}
