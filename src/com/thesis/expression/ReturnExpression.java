package com.thesis.expression;

import org.objectweb.asm.tree.InsnNode;

import java.io.IOException;
import java.io.Writer;

public class ReturnExpression extends Expression {

	Expression mExpression;

	public ReturnExpression(InsnNode node) {
		super(node);
	}

	public void setExpression(Expression expression) {
		mExpression = expression;
	}

	@Override
	public String getType() {
		return mExpression.getType();
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("return");
		if (mExpression != null) {
			writer.write(' ');
			mExpression.write(writer);
		}
	}
}
