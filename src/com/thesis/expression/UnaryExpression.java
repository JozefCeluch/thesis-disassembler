package com.thesis.expression;

import com.thesis.LocalVariable;
import org.objectweb.asm.tree.IincInsnNode;

import java.io.IOException;
import java.io.Writer;

public class UnaryExpression extends Expression {

	LocalVariable mLocalVariable;
	OpPosition mOpPosition;

	public UnaryExpression(IincInsnNode node, LocalVariable variable, String type, OpPosition pos) {
		super(node);
		mType = type;
		mLocalVariable = variable;
		mOpPosition = pos;
	}

	@Override
	public String getType() {
		return mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		// no preparation needed
		//todo remove previous if it's postfix, remove next if it's prefix
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(mOpPosition == OpPosition.PREFIX ? "++" : mLocalVariable.getName());
		writer.write(mOpPosition == OpPosition.PREFIX ? mLocalVariable.getName() : "++");
	}

	public static enum OpPosition {
		PREFIX, POSTFIX
	}
}
