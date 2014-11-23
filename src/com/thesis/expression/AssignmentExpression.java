package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;

import java.io.IOException;
import java.io.Writer;

public class AssignmentExpression extends  Expression{

	private LeftHandSide mLeftSide;
	private Expression mRightSide;

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

	@Override
	public void prepareForStack(ExpressionStack stack) {
		if (mRightSide != null) return;
		if (!stack.isEmpty() ) { //&& stack.peek().labelId == mLabel
			Expression rightSide = stack.pop(); // todo array assignment and type
//			if (localVar.hasDebugType()) {
//				rightSide.setType(localVar.getType());
//			}
			if (rightSide instanceof PrimaryExpression) {
				rightSide.setType(mLeftSide.getType());
			}
			mRightSide = rightSide;
		}
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
