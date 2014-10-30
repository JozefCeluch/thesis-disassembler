package com.thesis.expression;

import java.io.IOException;
import java.io.Writer;

public class LogicGateExpression extends ConditionalExpression {

	private LogicGateOperand mOperand;
	private ConditionalExpression  mLeft;
	private ConditionalExpression  mRight;

	public LogicGateExpression(LogicGateOperand operand, ConditionalExpression left, ConditionalExpression right) {
		mOperand = operand;
		mLeft = left;
		mRight = right;
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
