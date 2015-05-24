package com.thesis.translator;

import com.thesis.expression.*;
import org.objectweb.asm.Label;

import java.util.*;

/**
 * A stack that holds all instances of {@link Expression}
 * <p>
 * This class facilitates creation of more complex expressions from simpler ones.
 * Internally it uses a stack of {@link Item}s that wrap stored expressions.
 * On the outside, the classes that use this stack only work with expressions. The only exception is the
 * {@link StackEnhancer} interface that can also access the items directly.
 */
public class ExpressionStack {

	private static final int NOT_SET = -1;

	/**
	 * List of ExpressionStack enhancers
	 */
	private List<StackEnhancer> mEnhancers;

	/**
	 * Internal stack
	 */
	private final Stack<Item> mStack;

	/**
	 * Current line number
	 */
	private int mLineNum;

	/**
	 * Current label id
	 */
	private int mLabel;

	/**
	 * Last visited frame
	 */
	private int mVisitedFrame = NOT_SET;

	/**
	 * Position where the last stack improvement finished
	 */
	private int mLastImprovementPosition = 0;

	/**
	 * Map of {@link Label} with their IDs, only IDs are used in the program
	 */
	private Map<Label, Integer> mLabels;

	/**
	 * Map that maps the label id of the frame to {@link Item}
	 */
	private Map<Integer, Item> mFrameItemMap;

	/**
	 * Creates new instance of the stack, should be used only for the top-level stack
	 */
	public ExpressionStack() {
		mLabels = new HashMap<>();
		mStack = new Stack<>();
		mFrameItemMap = new HashMap<>();
		mEnhancers = new ArrayList<>();
	}

	/**
	 * Copy constructor
	 * @param original original expression stack
	 */
	private ExpressionStack(ExpressionStack original) {
		mLabels = original.mLabels;
		mEnhancers = original.mEnhancers;

		mLineNum = original.mLineNum;
		mLabels = original.mLabels;
		mLabel = original.mLabel;
		mLastImprovementPosition = 0;

		mStack = new Stack<>();
		mFrameItemMap = new HashMap<>();
	}

	/**
	 * @return copy of the current stack with empty intenral stack
	 */
	public ExpressionStack getNew() {
		return new ExpressionStack(this);
	}

	/**
	 * Adds an enhancer to the list
	 * @param enhancer stack enhancer
	 */
	public void addEnhancer(StackEnhancer enhancer) {
		mEnhancers.add(enhancer);
	}

	/**
	 * Starts the enhancement of the stack, should be executed at the end of the decompilation process, before
	 * converting expressions to statements.
	 */
	public void enhance() {
		if(mEnhancers.isEmpty()) {
			return;
		}
		for (StackEnhancer enhancer : mEnhancers) {
			enhancer.enhance(this, mStack);
		}
	}

	/**
	 * Pushes the expression to the stack
	 * @param expression expression to be pushed onto the stack
	 */
	public void push(Expression expression) {
		expression.setLine(mLineNum);
		expression.prepareForStack(this);
		mStack.push(new Item(expression, mLabel, mLineNum));
		improveStack();
		expression.afterPush(this);
		if (mVisitedFrame != NOT_SET) {
			mFrameItemMap.put(mVisitedFrame, mStack.peek());
			mVisitedFrame = NOT_SET;
		}
	}

	/**
	 * Pushes the expression to the stack
	 * @param expression expression to be pushed onto the stack
	 * @param shouldUpdateStack if false then it does not do any improvements of the stack before or after push
	 */
	public void push(Expression expression, boolean shouldUpdateStack) {
		if (shouldUpdateStack) {
			push(expression);
		} else {
			mStack.push(new Item(expression, mLabel, mLineNum));
		}
	}

	/**
	 * Improves the expressions on the stack
	 */
	private void improveStack() {
		for (int i = mLastImprovementPosition; i < mStack.size(); i++) { // think if ok
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

	/**
	 * Returns the expression on top of the stack
	 * @return the expression on top of the stack, without popping, null in case the stack is empty
	 */
	public Expression peek() {
		if (mStack.isEmpty()) return null;
		return mStack.peek().getExpression();
	}

	/**
	 * Pops the expression from the top of the stack
	 * @return null if the stack is empty, otherwise expression on top
	 */
	public Expression pop() {
		if (mStack.isEmpty()) return null;
		return mStack.pop().getExpression();
	}

	/**
	 * Returns the expression at the specified position in the stack
	 * @param index position
	 * @return object at the specified index
	 * @throws ArrayIndexOutOfBoundsException - if the index is out of range {@literal ( index < 0 || index >= size())}
	 */
	public Expression get(int index) {
		return mStack.get(index).getExpression();
	}

	/**
	 * Returns the item at given index
	 * @param index position
	 * @return item at the specified index
	 * @throws ArrayIndexOutOfBoundsException - if the index is out of range {@literal ( index < 0 || index >= size())}
	 */
	Item getItem(int index) {
		return mStack.get(index);
	}

	/**
	 * Swaps the two expressions on top of the stack, the expression on top will be the second
	 */
	public void swap() {
		Item first = mStack.pop();
		Item second = mStack.pop();
		mStack.push(first);
		mStack.push(second);
	}

	/**
	 * Adds all expressions from the given stack to the end of this stack
	 * @param stack to copy from
	 */
	public void addAll(ExpressionStack stack){
		if (stack == null) return;
		for(Item exp : stack.getAll()){
			mStack.push(exp);
		}
	}

	private List<Item> getAll() {
		return Arrays.asList(mStack.toArray(new Item[mStack.size()]));
	}

	/**
	 * Removes expression at given index
	 * @param index position
	 * @return removed expression
	 */
	public Expression remove(int index) {
		return mStack.remove(index).getExpression();
	}

	/**
	 * @param line currently processed line number
	 */
	public void setLineNumber(int line) {
		mLineNum = line;
	}

	/**
	 * @param label currently processed label
	 */
	public void setLabel(int label) {
		mLabel = label;
	}

	/**
	 * @return currently processed label
	 */
	public int getLabel() {
		return mLabel;
	}

	/**
	 * @return size of the stack
	 */
	public int size() {
		return mStack.size();
	}

	/**
	 * Adds the label to the map and returns its ID (ids are assigned sequentially)
	 * @param label that is to be added to the map
	 * @return id of the label in the map
	 */
	public int getLabelId(final Label label) {
		Integer labelId = mLabels.get(label);
		if (labelId == null) {
			labelId = mLabels.size();
			mLabels.put(label, labelId);
		}
		return labelId;
	}

	/**
	 * @return true if the stack is empty
	 */
	public boolean isEmpty() {
		return mStack.isEmpty();
	}

	/**
	 * Create the exact copy of the stack with the expressions
	 * @return copy of the stack with the same expressions
	 */
	public ExpressionStack duplicate() {
		ExpressionStack copy = getNew();
		copy.mStack.addAll(this.mStack);
		return copy;
	}

	/**
	 * Sets the given label id as the i of last visited frame
	 * @param currentLabel label id
	 */
	public void addFrame(int currentLabel) {
		mVisitedFrame = currentLabel;
	}

	/**
	 * Returns the index of the expression that is at the location of the frame
	 * @param label id
	 * @return -1 if no frame expression is found for the given label, or a valid expression index
	 */
	public int getExpressionIndexOfFrame(int label) {
		Item item = mFrameItemMap.get(label);
		if (item == null) {
			return -1;
		}
		return mStack.indexOf(item);
	}

	/**
	 * Creates new ExpressionStack from the current stack with the expressions in the given range.
	 * Expressions are removed from the original stack
	 * @param startIndex included
	 * @param endIndex not included
	 * @return new expression stack instance with the expressions in the range
	 */
	public ExpressionStack substack(int startIndex, int endIndex) {
		ExpressionStack subStack = getNew();
		for(int i = 0; i < endIndex - startIndex; i++ ) {
			subStack.mStack.push(mStack.remove(startIndex));
		}
		return subStack;
	}

	/**
	 * ExpressionStack item, that wraps the expressions pushed onto the stack.
	 *
	 * Allows replacing of the expressions inside it
	 */
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
