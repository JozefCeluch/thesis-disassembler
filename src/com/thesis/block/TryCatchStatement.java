package com.thesis.block;

import com.thesis.StatementCreator;
import com.thesis.expression.TryExpression;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class TryCatchStatement extends Statement {

	private BlockStatement mTryBlock;
	private List<CatchStatement> mCatchBlocks;
	private BlockStatement mFinallyBlock;

	public TryCatchStatement(TryExpression tryExpression, int line) {
		super(tryExpression, line);
		mTryBlock = new BlockStatement(line, new StatementCreator(tryExpression.getTryStack()).getStatements());
		if (tryExpression.getFinallyStack() != null && !tryExpression.getFinallyStack().isEmpty()) {
			mFinallyBlock = new BlockStatement(line, new StatementCreator(tryExpression.getFinallyStack()).getStatements());
		}
		mCatchBlocks = new ArrayList<>();
		for(TryExpression.CatchExpression catchExpression : tryExpression.getCatchExpressions()) {
			mCatchBlocks.add(new CatchStatement(catchExpression));
		}
	}

	@Override
	public Block disassemble() {
		return this;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("try");
		mTryBlock.write(writer);
		for (CatchStatement catchBlock : mCatchBlocks) {
			catchBlock.write(writer);
		}
		if (mFinallyBlock != null) {
			writer.write(" finally");
			mFinallyBlock.write(writer);
			writer.write(NL);
		} else {
			writer.write(NL);
		}
	}

	private class CatchStatement extends Statement {

		private BlockStatement mCatchBlock;

		protected CatchStatement(TryExpression.CatchExpression catchExpression) {
			super(catchExpression, catchExpression.getLine());
			mCatchBlock = new BlockStatement(this.mLine, new StatementCreator(catchExpression.getStack()).getStatements());
		}

		@Override
		public void write(Writer writer) throws IOException {
			mExpression.write(writer);
			mCatchBlock.write(writer);
		}
	}
}
