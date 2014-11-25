package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.tree.InsnNode;

import java.io.IOException;
import java.io.Writer;

public class ArrayAccessExpression extends Expression {

	private Expression indexExpression;
	private Expression arrayRef;

	public ArrayAccessExpression(InsnNode node) {
		super(node);
	}

	public void setIndexExpression(Expression indexExpression) {
		this.indexExpression = indexExpression;
	}

	public void setArrayRef(Expression arrayRef) {
		this.arrayRef = arrayRef;
	}

	@Override
	public DataType getType() {
		return arrayRef.getType();
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		indexExpression = stack.pop();
		arrayRef = stack.pop();
	}

	@Override
	public void write(Writer writer) throws IOException {
		arrayRef.write(writer);
		writer.write("[");
		indexExpression.write(writer);
		writer.write("]");
	}
}
