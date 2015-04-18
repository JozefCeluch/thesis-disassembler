package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.tree.JumpInsnNode;

import java.io.IOException;
import java.io.Writer;

public class UnconditionalJump extends JumpExpression {

	private boolean mIsVirtual;

	public UnconditionalJump(int opCode, int jumpLocation) {
		super(opCode, jumpLocation);
		mIsVirtual = true;
	}

	public UnconditionalJump(int opCode, int jumpLocation, ExpressionStack stack) {
		super(opCode, jumpLocation);
		mIsVirtual = false;
		mElseBranchEnd = jumpLocation;
		mStartFrameLocation = jumpLocation;
		thenBranch = stack;
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
