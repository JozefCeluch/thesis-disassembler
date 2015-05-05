package com.thesis.expression;

import com.thesis.expression.variable.Variable;
import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents the simplest expression that can be pushed to the stack
 * <p>
 * used for the following instructions:
 * BIPUSH, SIPUSH, LDC
 *
 * it is also used to wrap simple operands to expressions to be used in more complex expressions
 */
public class PrimaryExpression extends Expression {

	private static final String QUOTE = "\"";

	protected Object mValue;

	public PrimaryExpression(int opCode, Object value, DataType type) {
		super(opCode);
		mValue = value;
		mType = type;
		mIsVirtual = true;
	}

	public PrimaryExpression(Object value, DataType type) {
		this(0, value, type);
	}

	@Override
	public void setType(DataType type) {
		super.setType(type);
		if (mValue instanceof Variable) {
			((Variable)mValue).setType(type);
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		String output = printCast();

		if (DataType.BOOLEAN.equals(mType)){
			output += (int)mValue == 0 ? "false" : "true";
		} else if (DataType.getTypeFromObject("java.lang.String").equals(mType)) {
			output += QUOTE + mValue + QUOTE;
		} else if (DataType.getTypeFromObject("java.lang.Class").equals(mType) && mValue instanceof DataType) {
			output += ((DataType)mValue).print() + ".class";
		} else {
			output += mValue.toString();
			if (DataType.FLOAT.equals(mType)) output += "F";
			if (DataType.LONG.equals(mType)) output += "L";
		}

		writer.write(output);
	}

	protected String printCast() {
		return mCastType != null ? "(" + mCastType.print() + ") " : "";
	}

	@Override
	public DataType getType() {
		return mType;
	}

	public Object getValue() {
		return mValue;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		// no preparation necessary
	}

	public boolean isConstant() {
		return true;
	}
}
