package com.thesis.expression;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

import java.io.IOException;
import java.io.Writer;

public class MultiConditional extends ConditionalExpression {

	private Expression left;
	private Expression right;

	public MultiConditional(JumpInsnNode instruction, int jumpDestination, Expression rightExpression , Expression leftExpression, ExpressionStack then) {
		super(instruction, jumpDestination);
		super.thenBranch = then;
		left = leftExpression;
		right = rightExpression;
	}

	public MultiConditional(InsnNode instruction, int jumpDestination, Expression rightExpression, Expression leftExpression, ExpressionStack then) {
		super(instruction, jumpDestination);
		super.thenBranch = then;
		left = leftExpression;
		right = rightExpression;
	}

	@Override
	public void write(Writer writer) throws IOException {
		left.write(writer);
		writer.append(" ").append(mOperand.toString()).append(" ");
		right.write(writer);
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
	}
}
