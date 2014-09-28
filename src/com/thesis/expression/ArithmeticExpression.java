package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class ArithmeticExpression extends Expression {

	private Operand mOperand;
	private Expression mLeftSide;
	private Expression mRightSide;

	public ArithmeticExpression(AbstractInsnNode instruction, Operand operand, Expression leftSide, Expression rightSide) {
		super(instruction);
		mOperand = operand;
		mLeftSide = leftSide;
		mRightSide = rightSide;
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeftSide.write(writer);
		writer.append(" ").append(mOperand.toString()).append(" ");
		mRightSide.write(writer);
	}
}
