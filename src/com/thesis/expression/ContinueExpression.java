package com.thesis.expression;

import java.io.IOException;
import java.io.Writer;

public class ContinueExpression extends JumpExpression {

	public ContinueExpression(UnconditionalJump jump) {
		super(jump.mOpCode, jump.mJumpDestination);
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("continue");
	}

	@Override
	public boolean isVirtual() {
		return false;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {

	}
}
