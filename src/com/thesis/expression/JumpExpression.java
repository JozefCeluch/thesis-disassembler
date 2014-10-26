package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

public abstract class JumpExpression extends Expression {

	public int jumpLocation;

	public JumpExpression(AbstractInsnNode instruction, int jumpLocation) {
		super(instruction);
		this.jumpLocation = jumpLocation;
	}

}
