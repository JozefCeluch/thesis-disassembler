package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

public abstract class JumpExpression extends ConditionalExpression {

	public JumpExpression(AbstractInsnNode instruction, int jumpLocation) {
		super(instruction, jumpLocation);
	}

}
