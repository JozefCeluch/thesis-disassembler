package com.thesis.expression;

import com.thesis.common.DataType;

import java.io.IOException;
import java.io.Writer;

public class LogicGateExpression extends ConditionalExpression {

	private LogicGateOperand mOperand;
	private ConditionalExpression  mLeft;
	private ConditionalExpression  mRight;

	public LogicGateExpression(ConditionalExpression left, ConditionalExpression right) {
		super(right.getJumpDestination());
		setElseBranchEnd(right.getElseBranchEnd());
		mRight = right;
		mLeft = left;
		mType = DataType.BOOLEAN;
		updateBranches();
		if (mLeft.getJumpDestination() == right.getJumpDestination()) {
			mOperand = LogicGateOperand.AND;
		} else {
			mOperand = LogicGateOperand.OR;
			left.negate();
		}
		mThenBranchStart = right.getThenBranchStart();
	}

	public void updateBranches(){
		mLeft.thenBranch.addAll(mRight.thenBranch);
		mLeft.elseBranch.addAll(mRight.elseBranch);
		thenBranch = mLeft.thenBranch;
		elseBranch = mLeft.elseBranch;
	}

	@Override
	public DataType getType() {
		return mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeft.write(writer);
		writer.append(" ").append(mOperand.toString()).append(" ");
		mRight.write(writer);
	}
}
