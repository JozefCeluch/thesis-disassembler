package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class AssignmentExpression extends  Expression{

	private Expression mLeftSide;
	private Expression mRightSide;

	protected AssignmentExpression(AbstractInsnNode instruction, Expression leftSide, Expression rightSide) {
		super(instruction);
		mLeftSide = leftSide;
		mRightSide = rightSide;
	}

	public Expression getLeftSide() {
		return mLeftSide;
	}

	public void setLeftSide(Expression leftSide) {
		mLeftSide = leftSide;
	}

	public Expression getRightSide() {
		return mRightSide;
	}

	public void setRightSide(Expression rightSide) {
		mRightSide = rightSide;
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeftSide.write(writer);
		writer.append(" = ");
		mRightSide.write(writer);
	}
}
