package com.thesis.expression;

import com.thesis.LocalVariable;
import com.thesis.common.DataType;

import java.io.IOException;
import java.io.Writer;

public class VariableDeclarationExpression extends Expression {

	private LocalVariable mVariable;

	public VariableDeclarationExpression(LocalVariable variable) {
		super(null);
		mVariable = variable;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(mVariable.getType() + " " + mVariable.getName());
	}

	@Override
	public DataType getType() {
		return mVariable.getType();
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		//no preparation needed
	}
}
