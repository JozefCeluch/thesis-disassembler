package com.thesis.expression;

import com.thesis.StackEnhancer;
import org.objectweb.asm.Label;

import java.util.*;

public class ExpressionStack {

	private static final int NOT_SET = -1;

	private List<StackEnhancer> mEnhancers;

	private final Stack<StackItem> mStack;
	private int mLineNum;
	private Map<Label, Integer> mLabels;
	private int mLabel;
	private int mLastImprovementPosition = 0;
	private int mVisitedFrame = NOT_SET;
	private Map<Integer, StackItem> mFrameItemMap;

	public ExpressionStack(Map<Label, Integer> labels) {
		mLabels = labels;
		mStack = new Stack<>();
		mFrameItemMap = new HashMap<>();
		mEnhancers = new ArrayList<>();
	}

	private ExpressionStack(Map<Label, Integer> labels, List<StackEnhancer> enhancers) {
		mLabels = labels;
		mEnhancers = enhancers;
		mStack = new Stack<>();
		mFrameItemMap = new HashMap<>();
	}

	public ExpressionStack getNew() {
		return new ExpressionStack(mLabels, mEnhancers);
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
		mStack.push(new StackItem(expression, mLabel, mLineNum));
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
			mStack.push(new StackItem(expression, mLabel, mLineNum));
		}
	}

//	public void pushBelow(Expression expression, int position) {
//		//TODO preparation for DUP instructions
//	}

	private void improveStack() {
		for (int i = mLastImprovementPosition; i < mStack.size(); i++) { //todo think if ok
			Expression currentExp = mStack.get(i).getExpression();
			if (currentExp instanceof UnaryExpression) {
				if(((UnaryExpression) currentExp).mOpPosition == UnaryExpression.OpPosition.POSTFIX) {
					mStack.remove(i-1);
					mLastImprovementPosition = i - 1;
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
		StackItem first = mStack.pop();
		StackItem second = mStack.pop();
		mStack.push(first);
		mStack.push(second);
	}

	public void addAll(ExpressionStack stack){
		if (stack == null) return;
		for(StackItem exp : stack.getAll()){
			mStack.push(exp);
		}
	}

	public List<StackItem> getAll() {
		return Arrays.asList(mStack.toArray(new StackItem[mStack.size()]));
	}

//	public void clear() {
//		mStack.clear();
//	}

	public void remove(int index) {
		mStack.remove(index);
	}

	public void setLineNumber(int line) {
		mLineNum = line;
	}

	public void addLabel(Label label) {
		mLabel = getLabelId(label);
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
		ExpressionStack copy = new ExpressionStack(mLabels);
		copy.mStack.addAll(this.mStack);
		copy.mLineNum = this.mLineNum;
		copy.mLabels = this.mLabels;
		copy.mLabel = this.mLabel;
		copy.mLastImprovementPosition = this.mLastImprovementPosition;
		return copy;
	}

	public void addFrame(int currentLabel) {
		mVisitedFrame = currentLabel;
	}

	public int getExpressionIndexOfFrame(int label) {
		StackItem item = mFrameItemMap.get(label);
		if (item == null) {
			return -1;
		}
		return mStack.indexOf(item);
	}

	public ExpressionStack substack(int startIndex, int endIndex) {
		ExpressionStack subStack = new ExpressionStack(mLabels);
		for(int i = 0; i < endIndex - startIndex; i++ ) {
			subStack.mStack.push(mStack.remove(startIndex));
		}
		return subStack;
	}
}
