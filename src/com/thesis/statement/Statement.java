package com.thesis.statement;

import com.thesis.common.Writable;
import com.thesis.expression.Expression;

import java.io.IOException;
import java.io.Writer;

public class Statement implements Writable {

	protected static final String NL = "\n";
	protected static final String STATEMENT_END_NL = ";\n";
	protected static final String STATEMENT_END = "; ";
	protected Expression mExpression;
	protected int mLine;
	protected boolean mAddNewLine;
	protected boolean mWriteEnd;
	protected StringBuffer buf;

	protected Statement(int line) {
		mLine = line;
		buf = new StringBuffer();
	}

	public Statement(Expression expression, int line) {
		this(line);
		mExpression = expression;
		mAddNewLine = true;
		mWriteEnd = true;
	}

	@Override
	public void write(Writer writer) throws IOException {
		mExpression.write(writer);
		if (mWriteEnd) {
			writeEnd(writer);
		}
	}

	protected void writeEnd(Writer writer) throws IOException{
		writer.write(mAddNewLine ? STATEMENT_END_NL : STATEMENT_END);
	}
}
