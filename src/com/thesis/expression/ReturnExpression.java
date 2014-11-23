package com.thesis.expression;

import org.objectweb.asm.tree.InsnNode;

import java.io.IOException;
import java.io.Writer;

public class ReturnExpression extends Expression {

	private Expression mExpression;

	public ReturnExpression(InsnNode node, String type) {
		super(node);
		mType = type;
	}

	public void setExpression(Expression expression) {
		mExpression = expression;
	}

	@Override
	public String getType() {
		return mType.equals("ref") ? mExpression.getType() : mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		if (!mType.equals("void")) {
			Expression expression = stack.pop();
			if (expression instanceof ConditionalExpression && !getType().equals("boolean")) {
				expression = new TernaryExpression((ConditionalExpression) expression);
			}
			mExpression = expression;
		}
	}

	@Override
	public boolean isVirtual() {
		return mType.equals("void");
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (mType.equals("void")) return;
		writer.write("return");
		if (mExpression != null) {
			writer.write(' ');
			mExpression.write(writer);
		}
	}
}
