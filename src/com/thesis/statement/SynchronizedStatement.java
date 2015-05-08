package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.translator.StatementCreator;
import com.thesis.expression.MonitorExpression;

import java.io.IOException;
import java.io.Writer;

public class SynchronizedStatement extends Statement {

	private BlockStatement mBlockStatement;

	public SynchronizedStatement(MonitorExpression expression, int line, CodeElement parent) {
		super(expression, line, parent);
		mBlockStatement = new BlockStatement(line, expression.getSynchronizedBlock(), parent);
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.append(getTabs()).write("synchronized (");
		mExpression.write(writer);
		writer.write(")");
		mBlockStatement.write(writer);
		writer.write(NL);
	}
}
