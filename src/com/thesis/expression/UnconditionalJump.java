package com.thesis.expression;

import java.io.IOException;
import java.io.Writer;

public class UnconditionalJump extends JumpExpression {

	private boolean mIsVirtual;

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
