package com.thesis.expression;

import com.thesis.translator.ExpressionStack;
import com.thesis.common.DataType;
import com.thesis.translator.handler.TryCatchManager;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Expression that represents the try-catch block
 * <p>
 * it is not created by any bytecode instruction
 * try-catch blocks are stored separately in the class file, and ASM stores them in {@link org.objectweb.asm.tree.TryCatchBlockNode}
 */
public class TryCatchExpression extends Expression {

	private ExpressionStack mTryStack;
	private List<CatchExpression> mCatchExpressions;

	public TryCatchExpression(TryCatchManager.Item tryCatchItem) {
		super(0);
		mCatchExpressions = new ArrayList<>();

		mCatchExpressions.addAll(
				tryCatchItem.getCatchLocations().stream()
						.map(location -> new CatchExpression(location, tryCatchItem.getHandlerType(location), tryCatchItem.getCatchBlock(location)))
						.collect(Collectors.toList()));
		mTryStack = tryCatchItem.getTryStack();
	}

	public ExpressionStack getTryStack() {
		return mTryStack;
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
		private ArrayList<DataType> mExceptions;
		private AssignmentExpression mExpression;
		private ExpressionStack mStack;

		public CatchExpression(int label, ArrayList<String> exceptions, ExpressionStack stack) {
			super(0);
			mLabel = label;
			mStack = stack;
			mExpression = mStack != null ? (AssignmentExpression) mStack.remove(0) : null;
			if (exceptions != null && !exceptions.isEmpty()) {
				mExceptions = new ArrayList<>();
				for (String exceptionType : exceptions) {
					mExceptions.add(DataType.getTypeFromObject(exceptionType));
				}
			}
			if (mExceptions == null && mStack != null && mStack.size() > 0) {
				mStack.pop();
			}
		}

		public ExpressionStack getStack() {
			return mStack;
		}

		@Override
		public DataType getType() {
			return null;
		}

		@Override
		public void prepareForStack(ExpressionStack stack) {

		}

		@Override
		public void write(Writer writer) throws IOException {
			if (mExceptions != null) {
				writer.write(" catch (");

				int count = mExceptions.size();
				for (int i = 0; i < count; i++) {
					writer.append(mExceptions.get(i).toString()).append( i < count - 1 ? " | " : " ");
				}
				if (mExpression != null) {
					writer.write(mExpression.getVariable().toString());
				}
				writer.write(")");
			} else {
				writer.write(" finally");
			}
		}

		public int getLine() {
			return mLine;
		}
	}
}
