package com.thesis.expression;

import com.thesis.expression.variable.Variable;
import com.thesis.common.DataType;
import com.thesis.expression.stack.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents the unary increment operator
 * <p>
 * used for IINC instruction
 */
public class UnaryExpression extends Expression {
	Variable mVariable;
	private OpPosition mOpPosition;

	public UnaryExpression(int opCode, Variable variable, DataType type, OpPosition pos) {
		super(opCode);
		mType = type;
		mVariable = variable;
		mOpPosition = pos;
	}

	public boolean isPrefix() {
		return OpPosition.PREFIX.equals(mOpPosition);
	}

	public boolean isPostfix() {
		return OpPosition.POSTFIX.equals(mOpPosition);
	}

	public Variable getVariable() {
		return mVariable;
	}

	@Override
	public DataType getType() {
		return mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		// no preparation needed
		//todo remove previous if it's postfix, remove next if it's prefix
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(mOpPosition == OpPosition.PREFIX ? "++" : mVariable.toString());
		writer.write(mOpPosition == OpPosition.PREFIX ? mVariable.toString() : "++");
	}

	public enum OpPosition {
		PREFIX, POSTFIX
	}
}
