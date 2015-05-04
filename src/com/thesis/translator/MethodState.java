package com.thesis.translator;

import com.thesis.expression.JumpExpression;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class MethodState {
	private int mFrameLabel = JumpExpression.NO_DESTINATION;
	private int mCurrentLabel;
	private int mCurrentLine;
	private Set<Integer> mVisitedLabels;
	private AbstractInsnNode mCurrentNode;

	private ExpressionStack mStack;
	private Stack<ExpressionStack> mActiveStacks;

	public MethodState() {
		mVisitedLabels = new HashSet<>();
		mActiveStacks = new Stack<>();
		mStack = new ExpressionStack();
		mActiveStacks.push(mStack);
	}

	public ExpressionStack getFinalStack() {
		return mStack;
	}

	public ExpressionStack startNewStack() {
		ExpressionStack newStack = mActiveStacks.peek().getNew();
		mActiveStacks.push(newStack);
		return newStack;
	}

	public ExpressionStack getActiveStack() {
		return mActiveStacks.peek();
	}

	public void finishStack() {
		mActiveStacks.pop();
		ExpressionStack top = mActiveStacks.peek();
		top.setLineNumber(mCurrentLine);
	}

	public void replaceActiveStack(ExpressionStack newStack) {
		finishStack();
		mActiveStacks.push(newStack);
	}

	public int getFrameLabel() {
		return mFrameLabel;
	}

	public void setFrameLabel(int frameLabel) {
		mFrameLabel = frameLabel;
		getActiveStack().addFrame(frameLabel);
	}

	public int getCurrentLabel() {
		return mCurrentLabel;
	}

	public void setCurrentLabel(int currentLabel) {
		mCurrentLabel = currentLabel;
		getActiveStack().setLabel(currentLabel);
		mVisitedLabels.add(currentLabel);
	}

	public boolean isLabelVisited(int label) {
		return mVisitedLabels.contains(label);
	}

	public AbstractInsnNode getCurrentNode() {
		return mCurrentNode;
	}

	public void setCurrentNode(AbstractInsnNode currentNode) {
		mCurrentNode = currentNode;
	}

	public AbstractInsnNode moveNode() {
		if (mCurrentNode != null) {
			mCurrentNode = mCurrentNode.getNext();
		}
		return mCurrentNode;
	}

	public void setCurrentLine(int currentLine) {
		mCurrentLine = currentLine;
		getActiveStack().setLineNumber(mCurrentLine);
	}
}
