package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.util.Printer;

import java.io.IOException;
import java.io.Writer;

public class LogicalExpression extends ConditionalExpression {

	private Expression mLeftSide;
	private Expression mRightSide;

	public LogicalExpression(AbstractInsnNode instruction, Expression leftSide, Expression rightSide, int jumpDestination) {
		super(instruction, jumpDestination);
		mLeftSide = leftSide;
		mRightSide = rightSide;
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeftSide.write(writer);
		writer.append(" ").append(makeOperand().neg().toString()).append(" "); //todo NEG
		mRightSide.write(writer);
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {

	}
}
