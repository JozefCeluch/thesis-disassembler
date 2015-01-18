package com.thesis.expression;

import com.thesis.common.DataType;

import java.io.IOException;
import java.io.Writer;

public class TernaryExpression extends ConditionalExpression {

	private ConditionalExpression mCondition;
	private Expression mFirst;
	private Expression mSecond;

	public TernaryExpression(ConditionalExpression expression) {
		super(expression.mInstruction, expression.getConditionalJumpDest());
		mCondition = expression;
		mFirst = mCondition.getThenBranch().getAll().get(0).expression;
		mSecond = mCondition.getElseBranch().getAll().get(0).expression;
		if(mSecond instanceof ConditionalExpression) {
			mSecond = new TernaryExpression((ConditionalExpression) mSecond);
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
