package com.thesis.expression;

import org.objectweb.asm.Label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class ExpressionStack {

	private final Stack<StackExpression> mStack;
	private int mLineNum;
	private HashMap<Label, Integer> mLabels;
	private int mLabel;

	public ExpressionStack(HashMap<Label, Integer> labels) {
		mStack = new Stack<>();
		mLabels = labels;
	}

	public void push(Expression expression) {
		pushCompleteExp(expression);
	}

	public void push(ArrayAccessExpression expression) {
		Expression index = mStack.pop().expression;
		Expression arrayRef = mStack.pop().expression;
		expression.setIndexExpression(index);
		expression.setArrayRef(arrayRef);

	}

	public void push(ReturnExpression expression) {
//		if (mStack.size() >= 1) {
//			expression.setExpression(mStack.pop().expression);
//		}
		pushCompleteExp(expression);
	}

	public void push(ArithmeticExpression expression) {
		Expression right = mStack.pop().expression;
		Expression left = mStack.pop().expression;
		expression.setLeftSide(left);
		expression.setRightSide(right);
		pushCompleteExp(expression);
	}

	public void push(ArrayCreationExpression expression) {
		Expression length = mStack.pop().expression;
		expression.setLength(length);
		pushCompleteExp(expression);
	}

	public void push(AssignmentExpression expression) {
		if (!mStack.isEmpty() && mStack.peek().labelId == mLabel) {
			Expression rightSide = mStack.pop().expression; // todo array assignment and type
//			if (localVar.hasDebugType()) {
//				rightSide.setType(localVar.getType());
//			}
			expression.setRightSide(rightSide);
		}
		pushCompleteExp(expression);
	}

	public void push(UnaryExpression expression){
		//todo pop previous if it's postfix, skip next if it's prefix
		pushCompleteExp(expression);
	}

	public void push(MultiConditional expression) {
		Expression rightSide = mStack.pop().expression;
		Expression leftSide = mStack.pop().expression;
		expression.setLeft(leftSide);
		expression.setRight(rightSide);

//		pushLogicGateExpIfPossible(expression);
		pushCompleteExp(expression);
	}

	public void push(SingleConditional expression) {
		Expression left = mStack.pop().expression;
		expression.setLeft(left);

//		pushLogicGateExpIfPossible(expression);
		pushCompleteExp(expression);
	}

	public void push(UnconditionalJump expression) {
		pushCompleteExp(expression);
	}

	public Expression peek() {
		if (mStack.isEmpty()) return null;
		return mStack.peek().expression;
	}

	private void pushLogicGateExpIfPossible(ConditionalExpression expression) {
		if (conditionalExpressionsAreOnTop(mStack)) {
			StackExpression right = mStack.pop();
			LogicGateOperand operand;
			if (expression.getDestination() == right.labelId) {
				operand = LogicGateOperand.AND;
			} else {
				operand = LogicGateOperand.OR;
			}
			pushCompleteExp(new LogicGateExpression(operand, expression, (ConditionalExpression)right.expression, expression.getDestination()));
		} else {
			pushCompleteExp(expression);
		}
	}

	private void pushCompleteExp(Expression expression) {
		mStack.push(new StackExpression(expression, mLabel, mLineNum));
	}

	public void setLineNumber(int line) {
		mLineNum = line;
	}

	public void addLabel(Label label) {
		mLabel = getLabelId(label);
		System.out.println("Label: L" + mLabel);
	}

	public int size() {
		return mStack.size();
	}

//	private void putSimpleStackExpression(Expression expression) {
//		StackExpression exp = prepareStackExpression();
//		exp.expression = expression;
//		mStack.push(exp);
//	}
//
//	private StackExpression prepareStackExpression() {
//		if (mStack.empty()) throw new IllegalStateException("Stack cannot be empty");
//		StackExpression exp = mStack.peek();
//		StackExpression topExp = exp;
//		if (exp.expression != null) {
//			exp = new StackExpression(topExp.labelId);
//		} else {
//			exp = mStack.pop();
//		}
//		exp.line = topExp.line;
//		return exp;
//	}

	private boolean conditionalExpressionsAreOnTop(Stack<StackExpression> stack) {
		if (!stack.isEmpty()) {
			StackExpression stackTop = stack.peek();
			return stackTop.expression != null && (stackTop.expression instanceof ConditionalExpression);
		}
		return false;
	}

	public int getLabelId(final Label l) {
		Integer labelId = mLabels.get(l);
		if (labelId == null) {
			labelId = mLabels.size();
			mLabels.put(l, labelId);
		}
		return labelId;
	}

	private class StackExpression {

		public int labelId;
		public Expression expression;
		public int line;

		public StackExpression(Expression expression, int label, int line) {
			labelId = label;
			this.expression = expression;
			this.line = line;
		}
	}
}
