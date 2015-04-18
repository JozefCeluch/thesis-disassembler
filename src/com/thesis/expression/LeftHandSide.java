package com.thesis.expression;

import com.thesis.Variable;
import com.thesis.common.DataType;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class LeftHandSide extends Expression {

	private Variable mVariable;

	public LeftHandSide(int opCode, Variable variable) {
		super(opCode);
		mVariable = variable;
	}

	@Override
	public DataType getType() {
		return mVariable.getType();
	}

	@Override
	public void setType(DataType type) {
		super.setType(type);
		mVariable.setType(type);
	}

	@Override
	public boolean hasType() {
		return mVariable.hasType();
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		//no preparation necessary
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(mVariable.toString());
	}

	public Variable getVariable() {
		return mVariable;
	}
}
