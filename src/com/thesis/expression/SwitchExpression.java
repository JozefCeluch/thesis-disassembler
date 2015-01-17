package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class SwitchExpression extends Expression {

	private Expression mValue;
	private List<CaseExpression> mCaseList;

	public SwitchExpression(TableSwitchInsnNode instruction) {
		super(instruction);
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
	public void afterPush(ExpressionStack stack) {
		super.afterPush(stack);
	}

	@Override
	public void write(Writer writer) throws IOException {
		mValue.write(writer);
	}

	public static class CaseExpression extends Expression {

		public static final String DEFAULT = "default";

		private ExpressionStack mStack;
		private Object mValue;
		private int mLabel;
		private int mDefaultLabel;
		//TODO value can be number, String or enum
		public CaseExpression(Object value, int label, int defaultLabel, ExpressionStack stack) {
			super(null);
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
				writer.append("case ").append(mValue.toString()).write(":\n");
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
