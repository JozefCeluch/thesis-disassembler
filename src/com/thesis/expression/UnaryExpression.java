package com.thesis.expression;

import com.thesis.Variable;
import com.thesis.common.DataType;

import java.io.IOException;
import java.io.Writer;

public class UnaryExpression extends Expression {
//TODO make private?
	Variable mVariable;
	OpPosition mOpPosition;

	public UnaryExpression(int opCode, Variable variable, DataType type, OpPosition pos) {
		super(opCode);
		mType = type;
		mVariable = variable;
		mOpPosition = pos;
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
