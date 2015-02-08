package com.thesis.expression;

import com.thesis.Variable;
import com.thesis.common.DataType;
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
		mType = mLeftSide.getType();
	}

	public AssignmentExpression(AbstractInsnNode instruction, LeftHandSide leftSide, Expression rightSide) {
		this(instruction, leftSide);
		rightSide.setType(leftSide.getType());
		mRightSide = rightSide;
	}

	public void setRightSide(Expression rightSide) {
		mRightSide = rightSide;
	}

	public Expression getRightSide() {
		return mRightSide;
	}

	public Variable getVariable() {
		return mLeftSide.getVariable();
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeftSide.write(writer);
		writer.append(makeCorrectOperator());
		mRightSide.write(writer);
	}

	@Override
	public DataType getType() {
		return mLeftSide.getType();
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		if (mRightSide != null) return; //the expression already has left side
		if (!stack.isEmpty() ) { //&& stack.peek().labelId == mLabel
			mRightSide = stack.pop(); // todo array assignment and type
//			if (localVar.hasDebugType()) {
//				rightSide.setType(localVar.getType());
//			}
			if ((mRightSide.hasType() && !DataType.UNKNOWN.equals(mRightSide.getType()))
					&& (!mLeftSide.hasType() || DataType.UNKNOWN.equals(mLeftSide.getType()))) {
				mLeftSide.setType(mRightSide.getType());
			} else if ((mLeftSide.hasType() && !DataType.UNKNOWN.equals(mLeftSide.getType()))
					&& (!mRightSide.hasType() || DataType.UNKNOWN.equals(mRightSide.getType()))) {
				mRightSide.setType(mLeftSide.getType());
			}

			if (mRightSide.mCastType != null) {
				mLeftSide.setType(mRightSide.mCastType);
			} else if (mLeftSide.hasType() && !mLeftSide.getType().equals(mRightSide.getType())) {
				mRightSide.mCastType = mLeftSide.getType();
			}
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
