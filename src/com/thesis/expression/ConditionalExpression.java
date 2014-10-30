package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

public abstract class ConditionalExpression extends Expression {

	public ConditionalExpression(){

	}

	public ConditionalExpression(AbstractInsnNode instruction) {
		super(instruction);
	}

	@Override
	public String getType() {
		return "boolean";
	}
}
