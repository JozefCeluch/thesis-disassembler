package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.io.IOException;
import java.io.Writer;

public class AssignmentExpression extends  Expression{

	private Expression mRightSide;
	private Expression mLeftSide;

	public AssignmentExpression(AbstractInsnNode instruction, Expression rightSide) {
		super(instruction);
		mLeftSide = new PrimaryExpression(instruction);
		mRightSide = rightSide;
	}

	public AssignmentExpression(AbstractInsnNode instruction, Expression leftSide, Expression rightSide) {
		super(instruction);
		mLeftSide = leftSide;
		mRightSide = rightSide;
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeftSide.write(writer);
		writer.append(" = ");
		mRightSide.write(writer);
	}

	@Override
	public String getType() {
		return mRightSide.getType();
	}
}
