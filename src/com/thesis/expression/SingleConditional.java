package com.thesis.expression;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class SingleConditional extends ConditionalExpression {

	private Expression mLeftExpression;

	public SingleConditional(int opCode, int jumpDestination, Expression leftExpression, ExpressionStack then) {
		super(opCode, jumpDestination);
		super.thenBranch = then;
		mLeftExpression = leftExpression;
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (mOperand.equals(Operand.EQUAL)) {
			writer.write("!");
		}
		mLeftExpression.write(writer);
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
	}
}
