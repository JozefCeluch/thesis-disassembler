package com.thesis.expression;

import com.thesis.Variable;
import com.thesis.common.DataType;

import java.io.IOException;
import java.io.Writer;

public class VariablePrimaryExpression extends PrimaryExpression {

	public VariablePrimaryExpression(int opCode, Variable variable) {
		super(opCode, variable, variable.getType());
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(printCast());
		writer.write(mValue.toString());
	}
}