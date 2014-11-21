package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class SingleConditional extends ConditionalExpression {

	private Expression left;

	public SingleConditional(AbstractInsnNode instruction, int jumpDestination, ExpressionStack then) {
		super(instruction, jumpDestination);
		super.thenBranch = then;
	}

	public void setLeft(Expression left) {
		this.left = left;
	}

	@Override
	public void write(Writer writer) throws IOException {
		left.write(writer);
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		left = stack.pop();
	}
}
