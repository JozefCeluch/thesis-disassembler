package com.thesis.translator;

import com.thesis.expression.JumpExpression;
import com.thesis.expression.variable.LocalVariable;
import com.thesis.translator.handler.TryCatchManager;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.*;

public class MethodState {
	private AbstractInsnNode mCurrentNode;
	private int mCurrentLine;
	private int mCurrentLabel;
	private int mFrameLabel = JumpExpression.NO_DESTINATION;
	private Set<Integer> mVisitedLabels;
	private Map<Integer, LocalVariable> mLocalVariables;
	private ExpressionStack mStack;
	private Stack<ExpressionStack> mActiveStacks;
	private OnLabelChangeListener mOnLabelChangeListener;

	private TryCatchManager mTryCatchManager;

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

	public void updateCurrentLabel(int currentLabel) {
		mCurrentLabel = currentLabel;
		getActiveStack().setLabel(currentLabel);
		mVisitedLabels.add(currentLabel);
		if (mOnLabelChangeListener != null) {
			mOnLabelChangeListener.onLabelChange(currentLabel);
		}
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

	public Map<Integer, LocalVariable> getLocalVariables() {
		return mLocalVariables;
	}

	public void setLocalVariables(Map<Integer, LocalVariable> localVariables) {
		mLocalVariables = localVariables;
	}

	public void setOnLabelChangeListener(OnLabelChangeListener onLabelChangeListener) {
		mOnLabelChangeListener = onLabelChangeListener;
	}

	public interface OnLabelChangeListener {
		void onLabelChange(int newLabel);
	}

	public TryCatchManager getTryCatchManager() {
		return mTryCatchManager;
	}

	public void setupTryCatchManager(List tryCatchBlocks) {
		mTryCatchManager = TryCatchManager.newInstance(tryCatchBlocks, mStack);
	}
}