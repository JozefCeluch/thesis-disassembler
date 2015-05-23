package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents the short-circuit operators && and ||
 *<p>
 * in bytecode these operators do not have any special instructions,
 * this expression is inferred from the jump destinations of the processed conditional expression
 */
public class LogicGateExpression extends JumpExpression {

	private JumpExpression mLeft;
	private JumpExpression mRight;

	public LogicGateExpression(JumpExpression left, JumpExpression right) {
		super(0, right.getJumpDestination());
		mRight = right;
		mLeft = left;
		mType = DataType.BOOLEAN;
		updateBranches();
		if (mLeft.getJumpDestination() == right.getJumpDestination()) {
			mOperator = Operator.AND;
		} else {
			mOperator = Operator.OR;
			left.negate();
		}
		mThenBranchStart = right.getThenBranchStart();
		mElseBranchEnd = right.getElseBranchEnd();
		mStartFrameLocation = left.getStartFrameLocation();
	}

	public void updateBranches(){
		if (mLeft.thenBranch != null) {
			mLeft.thenBranch.addAll(mRight.thenBranch);
		} else {
			mLeft.thenBranch = mRight.thenBranch;
		}
		if (mLeft.elseBranch != null) {
			mLeft.elseBranch.addAll(mRight.elseBranch);
		} else {
			mLeft.elseBranch = mRight.elseBranch;
		}
		thenBranch = mLeft.thenBranch;
		elseBranch = mLeft.elseBranch;
	}

	@Override
	public void negate() {
		mLeft.negate();
		mRight.negate();
		mOperator = mOperator.neg();
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
		writer.append(" ").append(mOperator.toString()).append(" ");
		mRight.write(writer);
	}
}
