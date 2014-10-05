package com.thesis.expression;

import com.thesis.LocalVariable;

import java.io.IOException;
import java.io.Writer;

public class VariableDeclarationExpression extends Expression {

	private LocalVariable mVariable;

	public VariableDeclarationExpression(LocalVariable variable) {
		mVariable = variable;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(mVariable.getType() + " " + mVariable.getName());
	}

	@Override
	public String getType() {
		return mVariable.getType();
	}
}
