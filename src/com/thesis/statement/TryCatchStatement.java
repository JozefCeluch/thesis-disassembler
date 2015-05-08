package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.translator.StatementCreator;
import com.thesis.expression.TryCatchExpression;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class TryCatchStatement extends Statement {

	private BlockStatement mTryBlock;
	private List<CatchStatement> mCatchBlocks;

	public TryCatchStatement(TryCatchExpression tryCatchExpression, int line, CodeElement parent) {
		super(tryCatchExpression, line, parent);
		mTryBlock = new BlockStatement(line, tryCatchExpression.getTryStack(), this);
		mCatchBlocks = new ArrayList<>();
		for(TryCatchExpression.CatchExpression catchExpression : tryCatchExpression.getCatchExpressions()) {
			mCatchBlocks.add(new CatchStatement(catchExpression, parent));
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.append(getTabs()).write("try");
		mTryBlock.write(writer);
		for (CatchStatement catchBlock : mCatchBlocks) {
			catchBlock.write(writer);
		}
		writer.write(NL);
	}

	private class CatchStatement extends Statement {

		private BlockStatement mCatchBlock;

		protected CatchStatement(TryCatchExpression.CatchExpression catchExpression, CodeElement parent) {
			super(catchExpression, catchExpression.getLine(), parent);
			mCatchBlock = new BlockStatement(this.mLine, catchExpression.getStack(), this);
		}

		@Override
		public void write(Writer writer) throws IOException {
			mExpression.write(writer);
			mCatchBlock.write(writer);
		}
	}
}
