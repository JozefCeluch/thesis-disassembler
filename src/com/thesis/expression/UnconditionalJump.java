package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class UnconditionalJump extends JumpExpression {

	public UnconditionalJump(AbstractInsnNode instruction, int jumpLocation) {
		super(instruction, jumpLocation);
	}

	@Override
	public DataType getType() {
		return null;
	}

	@Override
	public void write(Writer writer) throws IOException {
	}

	@Override
	public boolean isVirtual() {
		return true;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		// no preparation needed
	}
}
