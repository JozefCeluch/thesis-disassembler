package com.thesis.expression;

import org.objectweb.asm.tree.LocalVariableNode;

import java.io.IOException;
import java.io.Writer;

public class PrimaryExpression extends Expression {

	Object mValue;

	public PrimaryExpression(Object value) {
		super();
		mValue = value;
	}

	@Override
	public void write(Writer writer) throws IOException {
		String output = mValue.toString();
		if (mValue instanceof LocalVariableNode) {
			output = ((LocalVariableNode)mValue).name;
		}
		writer.write(output);
	}
}
