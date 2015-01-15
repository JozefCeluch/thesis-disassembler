package com.thesis.expression;

import org.objectweb.asm.tree.JumpInsnNode;

import java.io.IOException;
import java.io.Writer;

public class BreakExpression extends JumpExpression {

	public BreakExpression(JumpInsnNode instruction, int jumpLocation) {
		super(instruction, jumpLocation);
	}

	public BreakExpression(UnconditionalJump jump) {
		super(jump.mInstruction, jump.mConditionalJumpDest);
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
