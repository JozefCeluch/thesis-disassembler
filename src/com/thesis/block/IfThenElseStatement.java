package com.thesis.block;

import com.thesis.expression.JumpExpression;

import java.io.IOException;
import java.io.Writer;

public class IfThenElseStatement extends IfThenStatement {

	protected Statement mElseStatement;

	public IfThenElseStatement(JumpExpression condition, int line){
		super(condition, line);
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
