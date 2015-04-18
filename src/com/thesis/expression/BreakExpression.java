package com.thesis.expression;

import org.objectweb.asm.tree.JumpInsnNode;

import java.io.IOException;
import java.io.Writer;

public class BreakExpression extends JumpExpression {

	public BreakExpression(int opCode, int jumpLocation) {
		super(opCode, jumpLocation);
	}

	public BreakExpression(UnconditionalJump jump) {
		super(jump.mOpCode, jump.mJumpDestination);
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("break");
	}

	@Override
	public boolean isVirtual() {
		return false;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {

	}
}
