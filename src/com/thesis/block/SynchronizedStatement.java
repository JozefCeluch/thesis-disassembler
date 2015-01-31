package com.thesis.block;

import com.thesis.StatementCreator;
import com.thesis.expression.MonitorExpression;

import java.io.IOException;
import java.io.Writer;

public class SynchronizedStatement extends Statement {

	private BlockStatement mBlockStatement;

	public SynchronizedStatement(MonitorExpression expression, int line) {
		super(expression, line);
		mBlockStatement = new BlockStatement(line,new StatementCreator(expression.getSynchronizedBlock()).getStatements());
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("synchronized (");
		mExpression.write(writer);
		writer.write(")");
		mBlockStatement.write(writer);
		writer.write(NL);
	}
}
