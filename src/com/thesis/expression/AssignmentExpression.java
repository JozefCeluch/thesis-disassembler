package com.thesis.expression;

import com.thesis.Variable;
import com.thesis.common.DataType;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.Writer;

public class AssignmentExpression extends  Expression{

	private LeftHandSide mLeftSide;
	private Expression mRightSide;

	public AssignmentExpression(int opCode, LeftHandSide leftSide) {
		super(opCode);
		mLeftSide = leftSide;
		mType = mLeftSide.getType();
	}

	public AssignmentExpression(int opCode, LeftHandSide leftSide, Expression rightSide) {
		this(opCode, leftSide);
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
		if (mRightSide != null) return; //the expression already has right side
		if (!stack.isEmpty() ) {
			mRightSide = stack.pop();

			if (mRightSide.hasType() && !mLeftSide.hasType()) {
				mLeftSide.setType(mRightSide.getType());
			} else if (mLeftSide.hasType() && mRightSide instanceof PrimaryExpression && ((PrimaryExpression) mRightSide).isConstant()) {
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
		if (mOpCode == Opcodes.IINC) {
			op = " += ";
		} else {
			op = " = ";
		}

		return op;
	}
}
