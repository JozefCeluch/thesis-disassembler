package com.thesis.expression;

import com.thesis.LocalVariable;

import java.io.IOException;
import java.io.Writer;

public class PrimaryExpression extends Expression {

	private String mType;
	private Object mValue;

	public PrimaryExpression(Object value) {
		super();
		mValue = value;
		mType = value.getClass().getSimpleName();
	}

	public PrimaryExpression(Object value, String type) {
		super();
		mValue = value;
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
