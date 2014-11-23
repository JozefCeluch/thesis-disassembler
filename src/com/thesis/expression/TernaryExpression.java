package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class TernaryExpression extends ConditionalExpression {

	private ConditionalExpression mExpression;

	public TernaryExpression(ConditionalExpression expression) {
		super(expression.mInstruction, expression.getConditionalJumpDest());
		mExpression = expression;
	}

	@Override
	public String getType() {
		return mExpression.getType();
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {

	}

	@Override
	public void write(Writer writer) throws IOException {
		mExpression.write(writer);
		writer.write(" ? ");
		mExpression.getThenBranch().getAll().get(0).expression.write(writer);
		writer.write(" : ");
		mExpression.getElseBranch().getAll().get(0).expression.write(writer);
	}
}
