package com.thesis.block;

import com.thesis.expression.Expression;

import java.io.IOException;
import java.io.Writer;

public class Statement extends Block {

	protected static final String STATEMENT_END_NL = ";\n";
	protected static final String STATEMENT_END = "; ";
	protected Expression mExpression;
	protected int mLine;
	protected boolean mAddNewLine;
	protected boolean mWriteEnd;

	protected Statement(int line) {
		mLine = line;
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

	public void setAddNewLine(boolean addNewLine) {
		mAddNewLine = addNewLine;
	}

	public void setWriteEnd(boolean writeEnd) {
		mWriteEnd = writeEnd;
	}

	@Override
	public Block disassemble() {
		return this;
	}

	protected void writeEnd(Writer writer) throws IOException{
		writer.write(mAddNewLine ? STATEMENT_END_NL : STATEMENT_END);
	}
}
