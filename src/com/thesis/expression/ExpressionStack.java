package com.thesis.expression;

import com.thesis.InstructionTranslator;
import org.objectweb.asm.Label;

import java.util.*;

public class ExpressionStack {

	private final Stack<StackItem> mStack;
	private int mLineNum;
	private HashMap<Label, Integer> mLabels;
	private int mLabel;
	private int mLastImprovementPosition = 0;

	public ExpressionStack() {
		mStack = new Stack<>();
		mLabels = InstructionTranslator.getLabels(); //TODO pass as argument in constructor
	}

	public void push(Expression expression) {
		expression.setLine(mLineNum);
		expression.prepareForStack(this); //TODO move prepare to expression
		mStack.push(new StackItem(expression, mLabel, mLineNum));
		improveStack();
		expression.afterPush(this);
	}

	public void push(Expression expression, boolean shouldUpdateStack) {
		if (shouldUpdateStack) {
			push(expression);
		} else {
			mStack.push(new StackItem(expression, mLabel, mLineNum));
		}
	}

	public void pushBelow(Expression expression, int position) {
		//TODO preparation for DUP instructions
	}

	private void improveStack() {
		for (int i = mLastImprovementPosition; i < mStack.size(); i++) { //todo think if ok
			Expression currentExp = mStack.get(i).expression;
			if (currentExp instanceof UnaryExpression) {
				if(((UnaryExpression) currentExp).mOpPosition == UnaryExpression.OpPosition.POSTFIX) {
					mStack.remove(i-1);
					mLastImprovementPosition = i - 1;
				} else if (i + 1 < mStack.size()) {
					mStack.remove(i+1);
					mLastImprovementPosition = i + 1;
				}
			} else {
//				mLastImprovementPosition = i;
			}
		}
	}

	public Expression peek() {
		if (mStack.isEmpty()) return null;
		return mStack.peek().expression;
	}

	public Expression pop() {
		if (mStack.isEmpty()) return null;
		return mStack.pop().expression;
	}

	public Expression get(int index) {
		return mStack.get(index).expression;
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

	public void clear() {
		mStack.clear();
	}

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

}
