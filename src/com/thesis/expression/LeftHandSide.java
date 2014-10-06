package com.thesis.expression;

import com.thesis.LocalVariable;

import java.io.IOException;
import java.io.Writer;

public class LeftHandSide extends Expression {

	private LocalVariable mLocalVariable;

	public LeftHandSide(LocalVariable localVar, String type) {
		mType = type;
		mLocalVariable = localVar;
	}

	@Override
	public String getType() {
		return mType;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(mLocalVariable.getName());
	}
}
