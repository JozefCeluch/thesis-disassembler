package com.thesis.block;

import com.thesis.expression.ConditionalExpression;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class IfThenStatement extends Statement {

	protected ConditionalExpression mCondition;
	protected Statement mThenStatement;

	public IfThenStatement(ConditionalExpression condition, int line){
		super(line);
		mCondition = condition;
	}

	public void setThenStatement(Statement thenStatement) {
		mThenStatement = thenStatement;
	}

	@Override
	public void write(Writer writer) throws IOException {
		StringWriter auxWriter = new StringWriter();
		mCondition.write(auxWriter);
		buf.setLength(0);
		buf.append("if (").append(auxWriter.toString()).append(")");
		writer.write(buf.toString());
		mThenStatement.write(writer);
		writer.write("\n");
	}
}
