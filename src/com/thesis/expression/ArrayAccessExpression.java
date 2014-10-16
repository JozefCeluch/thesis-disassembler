package com.thesis.expression;

import java.io.IOException;
import java.io.Writer;

public class ArrayAccessExpression extends Expression {

	private Expression indexExpression;
	private Expression arrayRef;

	public ArrayAccessExpression(Expression indexExpression, Expression arrayRef) {
		this.indexExpression = indexExpression;
		this.arrayRef = arrayRef;
	}

	@Override
	public String getType() {
		return arrayRef.getType();
	}

	@Override
	public void write(Writer writer) throws IOException {
		arrayRef.write(writer);
		writer.write("[");
		indexExpression.write(writer);
		writer.write("]");
	}
}
