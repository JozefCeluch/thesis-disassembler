package com.thesis.statement;

import com.thesis.translator.StatementCreator;
import com.thesis.expression.TryCatchExpression;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class TryCatchStatement extends Statement {

	private BlockStatement mTryBlock;
	private List<CatchStatement> mCatchBlocks;

	public TryCatchStatement(TryCatchExpression tryCatchExpression, int line) {
		super(tryCatchExpression, line);
		mTryBlock = new BlockStatement(line, new StatementCreator(tryCatchExpression.getTryStack()).getStatements());
		mCatchBlocks = new ArrayList<>();
		for(TryCatchExpression.CatchExpression catchExpression : tryCatchExpression.getCatchExpressions()) {
			mCatchBlocks.add(new CatchStatement(catchExpression));
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("try");
		mTryBlock.write(writer);
		for (CatchStatement catchBlock : mCatchBlocks) {
			catchBlock.write(writer);
		}
		writer.write(NL);
	}

	private class CatchStatement extends Statement {

		private BlockStatement mCatchBlock;

		protected CatchStatement(TryCatchExpression.CatchExpression catchExpression) {
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
