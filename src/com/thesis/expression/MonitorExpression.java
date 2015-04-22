package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.expression.stack.ExpressionStack;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.Writer;

public class MonitorExpression extends Expression {

	private Expression mSyncObjectExpression;
	private ExpressionStack mSynchronizedBlock;

	public MonitorExpression(int opCode) {
		super(opCode);
		mSynchronizedBlock = null;
	}

	public boolean isMonitorEnter() {
		return mOpCode == Opcodes.MONITORENTER;
	}

	public void setSynchronizedBlock(TryCatchExpression tryCatchExpression) {
		mSynchronizedBlock = tryCatchExpression.getTryStack();
	}

	public ExpressionStack getSynchronizedBlock() {
		return mSynchronizedBlock;
	}

	@Override
	public DataType getType() {
		return null;
	}

	@Override
	public boolean isVirtual() {
		return mSynchronizedBlock == null;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		mSyncObjectExpression = stack.pop();
		if (mSyncObjectExpression instanceof AssignmentExpression) {
			mSyncObjectExpression = ((AssignmentExpression) mSyncObjectExpression).getRightSide();
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		mSyncObjectExpression.write(writer);
	}
}
