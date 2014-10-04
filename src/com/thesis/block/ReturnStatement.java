package com.thesis.block;

import com.thesis.expression.Expression;

import java.io.IOException;
import java.io.Writer;

public class ReturnStatement extends Statement {
	Expression mExpression;

	public ReturnStatement(Expression expression) {
		mExpression = expression;
	}

	public ReturnStatement() {
	}

	@Override
	public Block disassemble() {
		return this;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("return ");
		if (mExpression != null) mExpression.write(writer);
		writeEnd(writer);
	}
}
