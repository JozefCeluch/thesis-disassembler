package com.thesis.translator;

import com.thesis.expression.JumpExpression;
import com.thesis.expression.variable.LocalVariable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

import java.util.*;

public class MethodState {
	private AbstractInsnNode mCurrentNode;
	private int mCurrentLine;
	private int mCurrentLabel;
	private int mFrameLabel = JumpExpression.NO_DESTINATION;
	private Set<Integer> mVisitedLabels;
	private Map<Integer, List<LocalVariable>> mLocalVariables;
	private ExpressionStack mStack;
	private Stack<ExpressionStack> mActiveStacks;
	private OnLabelChangeListener mOnLabelChangeListener;

	private TryCatchManager mTryCatchManager;

	public MethodState() {
		mVisitedLabels = new HashSet<>();
		mActiveStacks = new Stack<>();
		mStack = new ExpressionStack();
		mLocalVariables = new HashMap<>();
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

	public void addLocalVariable(int position, LocalVariable variable) {
		List<LocalVariable> variablesAtPos = mLocalVariables.get(position);
		if (variablesAtPos == null) {
			variablesAtPos = new ArrayList<>();
			variablesAtPos.add(variable);
			mLocalVariables.put(position, variablesAtPos);
		} else {
			int index = variablesAtPos.indexOf(variable);
			if (index > -1) {
				variablesAtPos.get(index).merge(variable);
			} else {
				variablesAtPos.add(variable);
			}
		}
	}

	Map<Integer, List<LocalVariable>> getLocalVariables () {
		return mLocalVariables;
	}

	public LocalVariable getLocalVariable(int position) {
		if (mLocalVariables == null || mLocalVariables.isEmpty()) {
			return null;
		}
		List<LocalVariable> varsAtPosition = mLocalVariables.get(position);
		if (varsAtPosition == null || varsAtPosition.isEmpty()) {
			return null;
		}
		int nearestLabelNode = findNearestLabelNode();
		for (LocalVariable var : varsAtPosition) {
			if (isInScope(var.getScopes(), nearestLabelNode, getActiveStack())) {
				return var;
			}
		}
		return null;
	}

	private boolean isInScope(List<LocalVariable.Scope> scopes, int nearestLabelNode, ExpressionStack stack) {
		if (scopes == null || scopes.isEmpty()) {
			return true;
		}
		for (LocalVariable.Scope scope : scopes) {
			int startLocation = scope.getStartLabelId(stack);
			int endLocation = scope.getEndLabelId(stack);
			if (startLocation == LocalVariable.Scope.UNDEFINED && endLocation == LocalVariable.Scope.UNDEFINED) return true;
			if (mVisitedLabels.contains(startLocation) && !mVisitedLabels.contains(endLocation)) return true;
			if (startLocation == nearestLabelNode) return true;
		}

		return false;
	}

	private int findNearestLabelNode() {
		AbstractInsnNode node = mCurrentNode;
		while (node != null && !(node instanceof LabelNode)) {
			node = node.getNext();
		}
		if (node != null) {
			return getActiveStack().getLabelId(((LabelNode)node).getLabel());
		}
		return -1;
	}
}
