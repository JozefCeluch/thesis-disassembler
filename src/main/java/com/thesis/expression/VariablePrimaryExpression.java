package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.expression.variable.Variable;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents the access to a variable
 * <p>
 * used for the following instructions:
 * GETFIELD, GETSTATIC, ILOAD, LLOAD, FLOAD, DLOAD, ALOAD
 */
public class VariablePrimaryExpression extends PrimaryExpression {

	public VariablePrimaryExpression(int opCode, Variable variable) {
		super(opCode, variable, variable.getType());
	}

	@Override
	public void setType(DataType type) {
		mType = type;
		((Variable)mValue).setType(type);
	}

	@Override
	public DataType getType() {
		return ((Variable)mValue).getType();
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
