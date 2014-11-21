package com.thesis.expression;

import com.thesis.LocalVariable;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class LeftHandSide extends Expression {

	private LocalVariable mLocalVariable;

	public LeftHandSide(AbstractInsnNode node, LocalVariable localVar) {
		super(node);
		mLocalVariable = localVar;
	}

	@Override
	public String getType() {
		return mLocalVariable.getType();
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		//no preparation necessary
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(mLocalVariable.getName());
	}
}
