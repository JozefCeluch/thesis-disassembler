package com.thesis.expression;

import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents a general unconditional jump
 * <p>
 * used for the GOTO instruction
 * It is never printed out directly into the Java code, used as a general jump that can be printed out in the form
 * of either a {@link BreakExpression} or a {@link ContinueExpression}
 */
public class UnconditionalJump extends JumpExpression {

	protected boolean mIsVirtual;

	public UnconditionalJump(int opCode, int jumpLocation) {
		super(opCode, jumpLocation);
		mIsVirtual = true;
	}

	@Override
	public void setThenBranch(ExpressionStack thenBranch) {
		super.setThenBranch(thenBranch);
		mIsVirtual = false;
		mElseBranchEnd = mJumpDestination;
		mStartFrameLocation = mJumpDestination;
	}

	@Override
	public void write(Writer writer) throws IOException {
	}

	@Override
	public boolean isVirtual() {
		return mIsVirtual;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		// no preparation needed
	}
}
