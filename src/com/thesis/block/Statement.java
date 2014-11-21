package com.thesis.block;

import com.thesis.expression.Expression;

import java.io.IOException;
import java.io.Writer;

public class Statement extends Block {

	protected static final String STATEMENT_END = ";\n";
	protected Expression mExpression;
	protected int mLine;


	protected Statement(int line) {
		mLine = line;
	}

	public Statement(Expression expression, int line) {
		this(line);
		mExpression = expression;
	}

	@Override
	public void write(Writer writer) throws IOException {
		mExpression.write(writer);
		writeEnd(writer);
	}

	@Override
	public Block disassemble() {
		return this;
	}

	protected void writeEnd(Writer writer) throws IOException{
		writer.write(STATEMENT_END);
	}
}
