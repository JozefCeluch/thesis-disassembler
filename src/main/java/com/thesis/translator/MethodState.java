package com.thesis.translator;

import com.thesis.expression.JumpExpression;
import com.thesis.expression.variable.LocalVariable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

import java.util.*;

/**
 * The context of the decompilation of a single method
 * <p>
 * The class holds all the information about the current state of the decompilation. It handles the
 * switching of {@link ExpressionStack}s, facilitates handling of local variables, keeps the reference
 * to the currently processed node and keeps the information about the position in the bytecode.
 */
public class MethodState {

	/**
	 * Interface that allows receive notifications when the label changes
	 */
	public interface OnLabelChangeListener {
		/**
		 * Called after the label was set
		 * @param newLabel label that was just set
		 */
		void onLabelChange(int newLabel);
	}

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

	/**
	 * @return top-level ExpressionStack
	 */
	public ExpressionStack getFinalStack() {
		return mStack;
	}

	/**
	 * Creates a new stack and sets it as active.
	 * The expressions are automatically being pushed onto the active stack.
	 * @return the newly created stack
	 */
	public ExpressionStack startNewStack() {
		ExpressionStack newStack = mActiveStacks.peek().getNew();
		mActiveStacks.push(newStack);
		return newStack;
	}

	/**
	 * @return the active stack, expressions should always be pushed onto active stack
	 */
	public ExpressionStack getActiveStack() {
		return mActiveStacks.peek();
	}

	/**
	 * Finishes the currently active stack, the previous stack is used as active
	 */
	public void finishStack() {
		mActiveStacks.pop();
		ExpressionStack top = mActiveStacks.peek();
		top.setLineNumber(mCurrentLine);
	}

	/**
	 * Replaces the currently active stack with the provided stack
	 * @param newStack stack to be used as active
	 */
	public void replaceActiveStack(ExpressionStack newStack) {
		finishStack();
		mActiveStacks.push(newStack);
	}

	/**
	 * @return label id of the lastly visited frame
	 */
	public int getFrameLabel() {
		return mFrameLabel;
	}

	/**
	 * Sets the provided label id as the current frame label
	 * @param labelId id of the last visited label
	 */
	public void setFrameLabel(int labelId) {
		mFrameLabel = labelId;
		getActiveStack().addFrame(labelId);
	}

	/**
	 * @return last visited label
	 */
	public int getCurrentLabel() {
		return mCurrentLabel;
	}

	/**
	 * Updates the current label and adds it to the visited labels
	 * @param currentLabel currently visited label
	 */
	public void updateCurrentLabel(int currentLabel) {
		mCurrentLabel = currentLabel;
		getActiveStack().setLabel(currentLabel);
		mVisitedLabels.add(currentLabel);
		if (mOnLabelChangeListener != null) {
			mOnLabelChangeListener.onLabelChange(currentLabel);
		}
	}

	/**
	 * @param label label id
	 * @return true if the label is in the visited labels set
	 */
	public boolean isLabelVisited(int label) {
		return mVisitedLabels.contains(label);
	}

	/**
	 * @return currently handled node
	 */
	public AbstractInsnNode getCurrentNode() {
		return mCurrentNode;
	}

	/**
	 * Sets the current node, should only be used at the setup, to move node use {@link MethodState#moveNode()}
	 * @param currentNode node
	 */
	public void setCurrentNode(AbstractInsnNode currentNode) {
		mCurrentNode = currentNode;
	}

	/**
	 * Moves the current node to the next
	 * @return the current node after moving
	 */
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

	public TryCatchManager getTryCatchManager() {
		return mTryCatchManager;
	}

	/**
	 * Creates a new instance of {@link TryCatchManager} from provided try-catch blocks
	 * @param tryCatchBlocks {@link org.objectweb.asm.tree.TryCatchBlockNode}s
	 */
	public void setupTryCatchManager(List tryCatchBlocks) {
		mTryCatchManager = TryCatchManager.newInstance(tryCatchBlocks, mStack);
	}

	/**
	 * Adds a local variable at the given position
	 * @param position variable position
	 * @param variable variable object
	 */
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

	/**
	 * @return map with local variables and their positions
	 */
	Map<Integer, List<LocalVariable>> getLocalVariables () {
		return mLocalVariables;
	}

	/**
	 * @param position position of the variable
	 * @return variable at the given position depending on the current position in the code
	 */
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
