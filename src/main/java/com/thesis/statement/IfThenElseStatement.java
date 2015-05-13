package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.expression.JumpExpression;

import java.io.IOException;
import java.io.Writer;

public class IfThenElseStatement extends IfThenStatement {

	protected Statement mElseStatement;

	public IfThenElseStatement(JumpExpression condition, int line, CodeElement parent){
		super(condition, line, parent);
	}

	public void setElseStatement(Statement elseStatement) {
		mElseStatement = elseStatement;
	}

	@Override
	public void write(Writer writer) throws IOException {
		super.writeIfThenStatement(writer);
		writer.write(" else");
		mElseStatement.write(writer);
		writer.write("\n");
	}
}
