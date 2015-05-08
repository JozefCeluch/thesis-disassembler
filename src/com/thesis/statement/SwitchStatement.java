package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.expression.Expression;
import com.thesis.expression.SwitchExpression;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public class SwitchStatement extends Statement {

	private BlockStatement mSwitchBlock;

	public SwitchStatement(SwitchExpression expression, int line, CodeElement parent) {
		super(expression, line, parent);
	}

	public void setSwitchBlock(BlockStatement switchBlock) {
		mSwitchBlock = switchBlock;
	}

	@Override
	public void write(Writer writer) throws IOException {
		StringWriter localWriter = new StringWriter();
		mExpression.write(localWriter);
		writer.append("switch (").append(localWriter.toString()).append(")");
		mSwitchBlock.write(writer);
		writer.write(NL);
	}

	public static class CaseStatement extends Statement {

		private List<Statement> mStatements;

		public CaseStatement(Expression expression, int line, List<Statement> statements, CodeElement parent) {
			super(expression, line, parent);
			mStatements = statements;
		}

		@Override
		public void write(Writer writer) throws IOException {
			writer.write(getTabs());
			mExpression.write(writer);
			for(Statement statement : mStatements) {
				statement.write(writer);
			}
		}
	}
}
