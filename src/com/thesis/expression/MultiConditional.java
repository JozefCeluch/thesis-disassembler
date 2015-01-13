package com.thesis.expression;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

import java.io.IOException;
import java.io.Writer;

public class MultiConditional extends ConditionalExpression {

	private Expression left;
	private Expression right;

	public MultiConditional(JumpInsnNode instruction, int jumpDestination, ExpressionStack then) {
		super(instruction, jumpDestination);
		super.thenBranch = then;
	}

	public MultiConditional(InsnNode instruction, int jumpDestination, ExpressionStack then) {
		super(instruction, jumpDestination);
		super.thenBranch = then;
	}

	@Override
	public void write(Writer writer) throws IOException {
		left.write(writer);
		writer.append(" ").append(makeOperand().neg().toString()).append(" "); //todo NEG
		right.write(writer);
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		right = stack.pop();
		left = stack.pop();
	}
}
