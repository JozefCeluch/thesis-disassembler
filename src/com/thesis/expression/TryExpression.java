package com.thesis.expression;

import com.thesis.TryCatchItem;
import com.thesis.common.DataType;
import com.thesis.expression.Expression;
import com.thesis.expression.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

public class TryExpression extends Expression {

	private TryCatchItem mTryCatchItem;

	public TryExpression(TryCatchItem tryCatchItem) {
		super(null);
		mTryCatchItem = tryCatchItem;
	}

	public TryCatchItem getTryCatchItem() {
		return mTryCatchItem;
	}

	@Override
	public DataType getType() {
		return null;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {

	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("TRYCATCHBLOCK");
	}
}
