package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.tree.InsnNode;

import java.io.IOException;
import java.io.Writer;

public class ReturnExpression extends Expression {

	private Expression mExpression;

	public ReturnExpression(InsnNode node, DataType type) {
		super(node);
		mType = type;
	}

	public void setExpression(Expression expression) {
		mExpression = expression;
	}

	@Override
	public DataType getType() {
		return mType.isReferenceType() ? mExpression.getType() : mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		if (!mType.equals(DataType.VOID)) {
			Expression expression = stack.pop();
			if (expression instanceof ConditionalExpression && !DataType.BOOLEAN.equals(getType())) {
				expression = new TernaryExpression((ConditionalExpression) expression);
			}
			mExpression = expression;
		}
	}

	@Override
	public boolean isVirtual() {
		return DataType.VOID.equals(mType);
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (DataType.VOID.equals(mType)) return;
		writer.write("return");
		if (mExpression != null) {
			writer.write(' ');
			mExpression.write(writer);
		}
	}
}
