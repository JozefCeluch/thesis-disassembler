package com.thesis.translator;

import com.thesis.expression.*;
import org.objectweb.asm.Label;

import java.util.*;

public class ExpressionStack {

	private static final int NOT_SET = -1;

	private List<StackEnhancer> mEnhancers;

	private final Stack<Item> mStack;
	private int mLineNum;
	private Map<Label, Integer> mLabels;
	private int mLabel;
	private int mLastImprovementPosition = 0;
	private int mVisitedFrame = NOT_SET;
	private Map<Integer, Item> mFrameItemMap;

	public ExpressionStack() {
		mLabels = new HashMap<>();
		mStack = new Stack<>();
		mFrameItemMap = new HashMap<>();
		mEnhancers = new ArrayList<>();
	}

	private ExpressionStack(ExpressionStack original) {
		mLabels = original.mLabels;
		mEnhancers = original.mEnhancers;

		mLineNum = original.mLineNum;
		mLabels = original.mLabels;
		mLabel = original.mLabel;
		mLastImprovementPosition = original.mLastImprovementPosition;

		mStack = new Stack<>();
		mFrameItemMap = new HashMap<>();
	}

	public ExpressionStack getNew() {
		return new ExpressionStack(this);
	}

	public void addEnhancer(StackEnhancer enhancer) {
		mEnhancers.add(enhancer);
	}

	public void enhance() {
		if(mEnhancers.isEmpty()) {
			return;
		}
		for (StackEnhancer enhancer : mEnhancers) {
			enhancer.enhance(this, mStack);
		}
	}

	public void push(Expression expression) {
		expression.setLine(mLineNum);
		expression.prepareForStack(this); //TODO move prepare to expression
		mStack.push(new Item(expression, mLabel, mLineNum));
		improveStack();
		expression.afterPush(this);
		if (mVisitedFrame != NOT_SET) {
			mFrameItemMap.put(mVisitedFrame, mStack.peek());
			mVisitedFrame = NOT_SET;
		}
	}

	public void push(Expression expression, boolean shouldUpdateStack) {
		if (shouldUpdateStack) {
			push(expression);
		} else {
			mStack.push(new Item(expression, mLabel, mLineNum));
		}
	}

//	public void pushBelow(Expression expression, int position) {
//		//TODO preparation for DUP instructions
//	}

	private void improveStack() {
		for (int i = mLastImprovementPosition; i < mStack.size(); i++) { //todo think if ok
			Expression currentExp = mStack.get(i).getExpression();
			if (currentExp instanceof UnaryExpression) {
				if(((UnaryExpression) currentExp).isPostfix()) {
					if (i > 0) {
						mStack.remove(i - 1);
						mLastImprovementPosition = i - 1;
					}
				} else if (i + 1 < mStack.size()) {
					mStack.remove(i+1);
					mLastImprovementPosition = i + 1;
				}
			} else if (currentExp instanceof MonitorExpression && mStack.size() > i+1) {
				Expression followingExp = mStack.get(i+1).getExpression();
				if (followingExp instanceof TryCatchExpression) {
					((MonitorExpression) currentExp).setSynchronizedBlock((TryCatchExpression)followingExp);
					mStack.remove(i+1);
					mLastImprovementPosition = i + 1;
				}
			}
		}
	}

	public Expression peek() {
		if (mStack.isEmpty()) return null;
		return mStack.peek().getExpression();
	}

	public Expression pop() {
		if (mStack.isEmpty()) return null;
		return mStack.pop().getExpression();
	}

	public Expression get(int index) {
		return mStack.get(index).getExpression();
	}

	public void swap() {
		Item first = mStack.pop();
		Item second = mStack.pop();
		mStack.push(first);
		mStack.push(second);
	}

	public void addAll(ExpressionStack stack){
		if (stack == null) return;
		for(Item exp : stack.getAll()){
			mStack.push(exp);
		}
	}

	public List<Item> getAll() {
		return Arrays.asList(mStack.toArray(new Item[mStack.size()]));
	}

//	public void clear() {
//		mStack.clear();
//	}

	public Expression remove(int index) {
		return mStack.remove(index).getExpression();
	}

	public void setLineNumber(int line) {
		mLineNum = line;
	}

	public void setLabel(int label) {
		mLabel = label;
	}

	public int getLabel() {
		return mLabel;
	}

	public int size() {
		return mStack.size();
	}

	public int getLabelId(final Label l) {
		Integer labelId = mLabels.get(l);
		if (labelId == null) {
			labelId = mLabels.size();
			mLabels.put(l, labelId);
		}
		return labelId;
	}

	public boolean isEmpty() {
		return mStack.isEmpty();
	}

	public ExpressionStack duplicate() {
		ExpressionStack copy = getNew();
		copy.mStack.addAll(this.mStack);
		return copy;
	}

	public void addFrame(int currentLabel) {
		mVisitedFrame = currentLabel;
	}

	public int getExpressionIndexOfFrame(int label) {
		Item item = mFrameItemMap.get(label);
		if (item == null) {
			return -1;
		}
		return mStack.indexOf(item);
	}

	public ExpressionStack substack(int startIndex, int endIndex) {
		ExpressionStack subStack = getNew();
		for(int i = 0; i < endIndex - startIndex; i++ ) {
			subStack.mStack.push(mStack.remove(startIndex));
		}
		return subStack;
	}

	public static class Item {

		private final int labelId;
		private Expression expression;
		private final int line;

		public Item(Expression expression, int label, int line) {
			labelId = label;
			this.expression = expression;
			this.line = line;
		}

		public int getLabelId() {
			return labelId;
		}

		public Expression getExpression() {
			return expression;
		}

		public void setExpression(Expression expression) {
			this.expression = expression;
		}

		public int getLine() {
			return line;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Item stackItem = (Item) o;

			if (labelId != stackItem.labelId) return false;
			if (line != stackItem.line) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = labelId;
			result = 31 * result + line;
			return result;
		}
	}
}
