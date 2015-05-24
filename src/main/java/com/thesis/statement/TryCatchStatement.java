package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.expression.TryCatchExpression;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A statement that represents a try-catch-finally block
 */
public class TryCatchStatement extends Statement {

	private BlockStatement mTryBlock;
	private List<CatchStatement> mCatchStatements;

	public TryCatchStatement(TryCatchExpression tryCatchExpression, int line, CodeElement parent) {
		super(tryCatchExpression, line, parent);
		mTryBlock = new BlockStatement(line, tryCatchExpression.getTryStack(), this);
		mCatchStatements = new ArrayList<>();
		for(TryCatchExpression.CatchExpression catchExpression : tryCatchExpression.getCatchExpressions()) {
			mCatchStatements.add(new CatchStatement(catchExpression, this));
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.append(getTabs()).write("try");
		mTryBlock.write(writer);
		for (CatchStatement catchStatement : mCatchStatements) {
			catchStatement.write(writer);
		}
		writer.write(NL);
	}

	/**
	 * A statement that represents a catch and a finally block (finally is a special case of a catch block)
	 */
	private class CatchStatement extends Statement {

		private BlockStatement mCatchBlock;

		protected CatchStatement(TryCatchExpression.CatchExpression catchExpression, CodeElement parent) {
			super(catchExpression, catchExpression.getLine(), parent);
			mCatchBlock = new BlockStatement(this.mLine, catchExpression.getStack(), parent);
		}

		@Override
		public void write(Writer writer) throws IOException {
			mExpression.write(writer);
			mCatchBlock.write(writer);
		}
	}
}
