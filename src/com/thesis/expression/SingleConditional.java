package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class SingleConditional extends ConditionalExpression {

	private Expression left;

	public SingleConditional(AbstractInsnNode instruction, int jumpDestination) {
		super(instruction, jumpDestination);
	}

	public void setLeft(Expression left) {
		this.left = left;
	}

	@Override
	public void write(Writer writer) throws IOException {
//		Expression rightSide = new PrimaryExpression(node, 0, "int");

	}
}
