package com.thesis.expression;

import com.thesis.Variable;
import com.thesis.common.DataType;
import org.objectweb.asm.tree.IincInsnNode;

import java.io.IOException;
import java.io.Writer;

public class UnaryExpression extends Expression {

	Variable mVariable;
	OpPosition mOpPosition;

	public UnaryExpression(IincInsnNode node, Variable variable, DataType type, OpPosition pos) {
		super(node);
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

	public static enum OpPosition {
		PREFIX, POSTFIX
	}
}
