package com.thesis.expression;

import com.thesis.common.DataType;

import java.io.IOException;
import java.io.Writer;

public class LogicGateExpression extends ConditionalExpression {

	private LogicGateOperand mOperand;
	private ConditionalExpression  mLeft;
	private ConditionalExpression  mRight;

	public LogicGateExpression(ConditionalExpression right) {
		super(right.getConditionalJumpDest());
		setGoToDest(right.getGoToDest());
		mRight = right;
		mType = DataType.BOOLEAN;
	}

	public void setOperand(LogicGateOperand operand) {
		mOperand = operand;
	}

	public void setLeft(ConditionalExpression left){
		mLeft = left;
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
		LogicGateOperand operand;
		operand = LogicGateOperand.AND; //todo operand
		mLeft = (ConditionalExpression)stack.pop();
		mOperand = operand;
		updateBranches();
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeft.write(writer);
		writer.append(" ").append(mOperand.toString()).append(" ");
		mRight.write(writer);
	}
}
