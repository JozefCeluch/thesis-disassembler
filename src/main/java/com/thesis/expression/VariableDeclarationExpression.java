package com.thesis.expression;

import com.thesis.expression.variable.LocalVariable;
import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents a declaration of a local variable
 * <p>
 * it is not used for any instruction
 */
public class VariableDeclarationExpression extends Expression {

	private LocalVariable mVariable;

	public VariableDeclarationExpression(LocalVariable variable) {
		super(0);
		mVariable = variable;
		mType = variable.getType();
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(mVariable.getType().print() + " " + mVariable.toString());
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
