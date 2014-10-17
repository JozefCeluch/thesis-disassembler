package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;

import java.io.IOException;
import java.io.Writer;

public class AssignmentExpression extends  Expression{

	private Expression mRightSide;
	private Expression mLeftSide;

	public AssignmentExpression(AbstractInsnNode instruction, LeftHandSide leftSide, Expression rightSide) {
		super(instruction);
		mLeftSide = leftSide;
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
		return mRightSide.getType();
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
