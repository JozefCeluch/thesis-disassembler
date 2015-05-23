package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Expression that represents the switch statement
 * <p>
 * used for the following instructions:
 * TABLESWITCH, LOOKUPSWITCH
 */
public class SwitchExpression extends Expression {

	private Expression mValue;
	private List<CaseExpression> mCaseList;

	public SwitchExpression(int opCode) {
		super(opCode);
		mCaseList = new ArrayList<>();
	}

	public void addCase(CaseExpression caseExp) {
		mCaseList.add(caseExp);
	}

	public List<CaseExpression> getCaseList() {
		return mCaseList;
	}

	@Override
	public DataType getType() {
		return null;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		mValue = stack.pop();
	}

	@Override
	public void write(Writer writer) throws IOException {
		mValue.write(writer);
	}

	/**
	 * Expression that represents a case block of a switch
	 */
	public static class CaseExpression extends Expression {

		public static final String DEFAULT = "default";
		public static final String CASE = "case ";

		private ExpressionStack mStack;
		private Object mValue;
		private int mLabel;
		private int mDefaultLabel;

		public CaseExpression(Object value, int label, int defaultLabel, ExpressionStack stack) {
			super(0);
			mValue = value;
			mStack = stack;
			mLabel = label;
			mDefaultLabel = defaultLabel;
		}

		public ExpressionStack getStack() {
			return mStack;
		}

		public void write(Writer writer) throws IOException {
			if (isDefaultCase()) {
				writer.write(DEFAULT + ":\n");
			} else {
				writer.append(CASE).append(mValue.toString()).write(":\n");
			}
		}

		public int getLabel() {
			return mLabel;
		}

		private boolean isDefaultCase() {
			return mLabel == mDefaultLabel;
		}

		@Override
		public DataType getType() {
			return null;
		}

		@Override
		public void prepareForStack(ExpressionStack stack) {

		}


	}
}
