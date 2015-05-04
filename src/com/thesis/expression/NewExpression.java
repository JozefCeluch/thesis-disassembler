package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents the new keyword
 *<p>
 * used for the NEW instruction
 */
public class NewExpression extends Expression {

	private Expression mExpression;

	public NewExpression(int opCode, String desc) {
		super(opCode);
		mType = DataType.getTypeFromObject(desc);
	}

	@Override
	public DataType getType() {
		return mType;
	}

	public void setExpression(Expression expression) {
		mExpression = expression;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {

	}

	@Override
	public void write(Writer writer) throws IOException {
		if (mExpression != null) {
			writer.write("new ");
			mExpression.write(writer);
		}
	}

	@Override
	public boolean isVirtual() {
		return mExpression == null;
	}

	@Override
	public String toString() {
		return "new " + mType;
	}
}
