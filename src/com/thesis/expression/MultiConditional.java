package com.thesis.expression;

import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents a comparison of two values
 * <p>
 * used for the following instructions:
 * IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, IFNULL, IFNONNULL,
 * LCMP, FCMPL, FCMPG, DCMPL, DCMPG
 */
public class MultiConditional extends JumpExpression {

	private Expression left;
	private Expression right;

	public MultiConditional(int opCode, int jumpDestination, Expression rightExpression , Expression leftExpression, ExpressionStack then) {
		super(opCode, jumpDestination);
		super.thenBranch = then;
		left = leftExpression;
		right = rightExpression;
	}

	public MultiConditional(int opCode, int jumpDestination, Expression rightExpression , Expression leftExpression) {
		super(opCode, jumpDestination);
		left = leftExpression;
		right = rightExpression;
	}

	@Override
	public void write(Writer writer) throws IOException {
		left.write(writer);
		writer.append(" ").append(mOperand.toString()).append(" ");
		right.write(writer);
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
	}
}
