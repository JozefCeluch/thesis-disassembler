package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;

import java.io.IOException;
import java.io.Writer;

public class AssignmentExpression extends  Expression{

	private Expression mRightSide;
	private LeftHandSide mLeftSide;

	public AssignmentExpression(AbstractInsnNode instruction, LeftHandSide leftSide) {
		super(instruction);
		mLeftSide = leftSide;
	}

	public AssignmentExpression(AbstractInsnNode instruction, LeftHandSide leftSide, Expression rightSide) {
		this(instruction, leftSide);
		mRightSide = rightSide;
	}

	public void setRightSide(Expression rightSide) {
		mRightSide = rightSide;
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeftSide.write(writer);
		writer.append(makeCorrectOperator());
		mRightSide.write(writer);
	}

	@Override
	public String getType() {
		return mLeftSide.getType();
	}

	private String makeCorrectOperator() {
		String op;
		if (mInstruction instanceof IincInsnNode) {
			op = " += ";
		} else {
			op = " = ";
		}

		return op;
	}
}
