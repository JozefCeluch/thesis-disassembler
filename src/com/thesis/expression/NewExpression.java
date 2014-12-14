package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class NewExpression extends Expression {

	private Expression mExpression;

	public NewExpression(AbstractInsnNode instruction, DataType type) {
		super(instruction);
		mType = type;
	}

	@Override
	public DataType getType() {
		return mType;
	}

	public void setExpression(Expression expression) {
		mExpression = expression;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {

	}

	@Override
	public void write(Writer writer) throws IOException {
		if (mExpression != null) {
			writer.write("new ");
			mExpression.write(writer);
		}
	}

	@Override
	public boolean isVirtual() {
		return mExpression == null;
	}

	@Override
	public String toString() {
		return "new " + mType;
	}
}
