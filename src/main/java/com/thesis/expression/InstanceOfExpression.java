package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents the Java instanceof keyword
 *<p>
 * used for the INSTANCEOF instruction
 */
public class InstanceOfExpression extends Expression {

	private Expression leftObject;
	private DataType rightClass;

	public InstanceOfExpression(int opCode, String desc) {
		super(opCode);
		rightClass = DataType.getTypeFromObject(desc);
	}

	@Override
	public DataType getType() {
		return DataType.BOOLEAN;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		leftObject = stack.pop();
	}

	@Override
	public void write(Writer writer) throws IOException {
		leftObject.write(writer);
		writer.append(" instanceof ").append(rightClass.toString());
	}
}
