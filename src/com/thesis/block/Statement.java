package com.thesis.block;

import com.thesis.block.Block;
import com.thesis.expression.Expression;

import java.io.IOException;
import java.io.Writer;

public class Statement extends Block {

	protected static final String STATEMENT_END = ";\n";
	Expression mExpression;

	protected Statement() {
	}

	public Statement(Expression expression) {
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
