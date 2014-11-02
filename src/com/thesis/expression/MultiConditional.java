package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class MultiConditional extends ConditionalExpression {

	private Expression left;
	private Expression right;

	public MultiConditional(AbstractInsnNode instruction, int jumpDestination, ExpressionStack then) {
		super(instruction, jumpDestination);
		super.thenBranch = then;
	}

	public void setLeft(Expression left) {
		this.left = left;
	}

	public void setRight(Expression right) {
		this.right = right;
	}

	@Override
	public void write(Writer writer) throws IOException {
		left.write(writer);
		writer.append(" ").append(makeOperand().neg().toString()).append(" "); //todo NEG
		right.write(writer);
	}
}
