package com.thesis.block;

import com.thesis.expression.ConditionalExpression;
import com.thesis.expression.Expression;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class IfThenElseStatement extends Statement {

	ConditionalExpression mCondition;
	Statement mThenStatement;
	Statement mElseStatement;

	public IfThenElseStatement(ConditionalExpression condition){
		mCondition = condition;
	}

	public void setThenStatement(Statement thenStatement) {
		mThenStatement = thenStatement;
	}

	public void setElseStatement(Statement elseStatement) {
		mElseStatement = elseStatement;
	}

	@Override
	public void write(Writer writer) throws IOException {
		StringWriter auxWriter = new StringWriter();
		mCondition.write(auxWriter);
		buf.setLength(0);
		buf.append("if (").append(auxWriter.toString()).append(") ");
		writer.write(buf.toString());
		mThenStatement.write(writer);
		if (mElseStatement != null) {
			writer.write(" else ");
			mElseStatement.write(writer);
			writer.write("\n");
		}
	}
}
