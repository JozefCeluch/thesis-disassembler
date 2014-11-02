package com.thesis.expression;

import java.io.IOException;
import java.io.Writer;

public class LogicGateExpression extends ConditionalExpression {

	private LogicGateOperand mOperand;
	private ConditionalExpression  mLeft;
	private ConditionalExpression  mRight;

	public LogicGateExpression(LogicGateOperand operand, ConditionalExpression right, int jumpDestination) {
		super(jumpDestination);
		mOperand = operand;
		mRight = right;
		mType = "boolean";
	}

	public void setLeft(ConditionalExpression left){
		mLeft = left;
	}

	public void updateBranches(){
		mLeft.thenBranch.addAll(mRight.thenBranch);
		if (mLeft.elseBranch != null) {
			mLeft.elseBranch.addAll(mRight.elseBranch);
		} else {
			mLeft.elseBranch = mRight.elseBranch;
		}
		thenBranch = mLeft.thenBranch;
		elseBranch = mLeft.elseBranch;
		mLeft.thenBranch = mLeft.elseBranch = mRight.thenBranch = mRight.elseBranch = null;
	}

	@Override
	public String getType() {
		return "boolean";
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeft.write(writer);
		writer.append(" ").append(mOperand.toString()).append(" ");
		mRight.write(writer);
	}
}
