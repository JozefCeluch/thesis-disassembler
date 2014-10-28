package com.thesis.expression;

import java.io.IOException;
import java.io.Writer;

public class ReturnExpression extends Expression {
	Expression mExpression;

	public ReturnExpression(Expression expression) {
		mExpression = expression;
	}

	@Override
	public String getType() {
		return mExpression.getType();
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("return");
		if (mExpression != null) {
			writer.write(' ');
			mExpression.write(writer);
		}
	}
}
