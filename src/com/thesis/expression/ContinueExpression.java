package com.thesis.expression;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents the Java continue keyword
 * <p>
 * a special case of {@link UnconditionalJump}
 */
public class ContinueExpression extends UnconditionalJump {

	public ContinueExpression(UnconditionalJump jump) {
		super(jump.mOpCode, jump.mJumpDestination);
		mIsVirtual = false;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("continue");
	}
}
