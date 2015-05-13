package com.thesis.expression;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents the break keyword
 *<p>
 * a special case of {@link UnconditionalJump}
 * used to in loops and switches
 */
public class BreakExpression extends UnconditionalJump {

	public BreakExpression(UnconditionalJump jump) {
		super(jump.mOpCode, jump.mJumpDestination);
		mIsVirtual = false;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("break");
	}
}
