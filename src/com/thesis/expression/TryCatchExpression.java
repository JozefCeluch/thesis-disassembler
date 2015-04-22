package com.thesis.expression;

import com.thesis.expression.stack.ExpressionStack;
import com.thesis.translator.TryCatchItem;
import com.thesis.common.DataType;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TryCatchExpression extends Expression {

	private ExpressionStack mTryStack;
	private List<CatchExpression> mCatchExpressions;
	private ExpressionStack mFinallyStack;

	public TryCatchExpression(TryCatchItem tryCatchItem) {
		super(0);
		mCatchExpressions = new ArrayList<>();

		mCatchExpressions.addAll(
				tryCatchItem.getHandlerLocations().stream()
						.map(location -> new CatchExpression(location, tryCatchItem.getHandlerType(location), tryCatchItem.getCatchBlock(location)))
						.collect(Collectors.toList()));
		mTryStack = tryCatchItem.getTryStack();
	}

	public ExpressionStack getTryStack() {
		return mTryStack;
	}

	public ExpressionStack getFinallyStack() {
		return mFinallyStack;
	}

	public List<CatchExpression> getCatchExpressions() {
		return mCatchExpressions;
	}

	@Override
	public void write(Writer writer) throws IOException {
	}

	@Override
	public DataType getType() {
		return null;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {

	}

	public class CatchExpression extends Expression {

		private int mLabel;
		private DataType mType;
		private AssignmentExpression mExpression;
		private ExpressionStack mStack;

		public CatchExpression(int label, String typeString, ExpressionStack stack) {
			super(0);
			mLabel = label;
			mType = typeString != null ? DataType.getTypeFromObject(typeString) : null;
			mStack = stack;
			mExpression = (AssignmentExpression) mStack.remove(0);
			if (mType == null && mStack.size() > 0) {
				mStack.pop();
			}
		}

		public ExpressionStack getStack() {
			return mStack;
		}

		@Override
		public DataType getType() {
			return mType;
		}

		@Override
		public void prepareForStack(ExpressionStack stack) {

		}

		@Override
		public void write(Writer writer) throws IOException {
			if (mType != null) {
				writer.append(" catch (")
						.append(mType.toString()).append(" ").append(mExpression.getVariable().toString())
						.append(")");
			} else {
				writer.write(" finally");
			}
		}

		public int getLine() {
			return mLine;
		}
	}
}
