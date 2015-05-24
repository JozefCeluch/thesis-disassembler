package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents the ternary operator ?:
 * <p>
 * it is only a shorthand for a if-then-else op therefore it is not generated for any specific instruction
 * it is only inferred from the {@link JumpExpression}
 */
public class TernaryExpression extends JumpExpression {

	private JumpExpression mCondition;
	private Expression mFirst;
	private Expression mSecond;

	public TernaryExpression(JumpExpression expression) {
		super(expression.mOpCode, expression.getJumpDestination());
		mCondition = expression;
		mFirst = mCondition.getThenBranch().get(0);
		mSecond = mCondition.getElseBranch().get(0);
		if(mSecond instanceof JumpExpression) {
			mSecond = new TernaryExpression((JumpExpression) mSecond);
		}
		setType(mFirst.getType());
	}

	@Override
	public void setType(DataType type) {
		super.setType(type);
		mFirst.setType(type);
		mSecond.setType(type);
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {

	}

	@Override
	public void write(Writer writer) throws IOException {
		mCondition.write(writer);
		writer.write(" ? ");
		mFirst.write(writer);
		writer.write(" : ");
		mSecond.write(writer);
	}
}
