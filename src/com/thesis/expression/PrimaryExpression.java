package com.thesis.expression;

import com.thesis.LocalVariable;

import java.io.IOException;
import java.io.Writer;

public class PrimaryExpression extends Expression {

	private static final String QUOTE = "\"";

	private Object mValue;

	public PrimaryExpression(Object value) {
		mValue = value;
	}

	public PrimaryExpression(Object value, String type) {
		mValue = value;
		mType = type;
	}

	public PrimaryExpression(String value, String type) {
		mValue = QUOTE + value + QUOTE;
		mType = type;
	}

	public PrimaryExpression(LocalVariable value, String type) {
		super();
		mValue = value;
		value.setType(type);
		mType = type;
	}

	@Override
	public void write(Writer writer) throws IOException {
		String output = mValue.toString();
		if (mValue instanceof LocalVariable) {
			output = ((LocalVariable)mValue).getName();
		}
		writer.write(output);
	}

	@Override
	public String getType() {
		return mType;
	}
}
