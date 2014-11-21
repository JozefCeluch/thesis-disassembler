package com.thesis.expression;

import com.thesis.InstructionTranslator;
import com.thesis.block.Block;
import com.thesis.block.BlockStatement;
import com.thesis.block.IfThenElseStatement;
import com.thesis.block.Statement;
import org.objectweb.asm.Label;

import java.util.*;

public class ExpressionStack {

	private final Stack<StackExpression> mStack;
	private int mLineNum;
	private HashMap<Label, Integer> mLabels;
	private int mLabel;

	public ExpressionStack() {
		mStack = new Stack<>();
		mLabels = InstructionTranslator.getLabels();
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
		if (!mStack.isEmpty()) { //&& mStack.peek().labelId == mLabel
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

		pushCompleteExp(expression);
	}

	public void push(SingleConditional expression) {
		Expression left = mStack.pop().expression;
		expression.setLeft(left);

		pushCompleteExp(expression);
	}

	public void push(UnconditionalJump expression) {
		pushCompleteExp(expression);
	}

	public void push(LogicGateExpression expression) {
		LogicGateOperand operand;
		operand = LogicGateOperand.AND;
		Expression left = mStack.pop().expression;
		expression.setLeft((ConditionalExpression)left);
		expression.setOperand(operand);
		expression.updateBranches();
		pushCompleteExp(expression);
	}

	public Expression peek() {
		if (mStack.isEmpty()) return null;
		return mStack.peek().expression;
	}

	public Expression pop() {
		if (mStack.isEmpty()) return null;
		return mStack.pop().expression;
	}

	private List<StackExpression> getAll() {
		return Arrays.asList(mStack.toArray(new StackExpression[mStack.size()]));
	}

	public void addAll(ExpressionStack stack){
		if (stack == null) return;
		for(StackExpression exp : stack.getAll()){
			mStack.push(exp);
		}
	}

	public void clear() {
		mStack.clear();
	}

//	private void pushLogicGateExpIfPossible(ConditionalExpression expression) {
//		if (conditionalExpressionsAreOnTop(mStack)) {
//			StackExpression right = mStack.pop();
//			LogicGateOperand operand;
//			if (expression.getDestination() == right.labelId) {
//				operand = LogicGateOperand.AND;
//			} else {
//				operand = LogicGateOperand.OR;
//			}
//			pushCompleteExp(new LogicGateExpression(operand, expression, (ConditionalExpression)right.expression, expression.getDestination()));
//		} else {
//			pushCompleteExp(expression);
//		}
//	}

	private void pushCompleteExp(Expression expression) {
		mStack.push(new StackExpression(expression, mLabel, mLineNum));
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

	public boolean isEmpty() {
		return mStack.isEmpty();
	}

	public List<Statement> getStatements() {
		List<Statement> statements = new ArrayList<>();
		for (StackExpression item : mStack) {
			//TODO this beauty probably does what it should, but WTF!! REFACTOR
			if (item.expression instanceof ConditionalExpression) {
				if (((ConditionalExpression) item.expression).getThenBranch().size() == 1
						&& ((ConditionalExpression) item.expression).getElseBranch().size() == 1
						&& ((ConditionalExpression) item.expression).getThenBranch().peek() instanceof PrimaryExpression
						&& ((ConditionalExpression) item.expression).getElseBranch().peek() instanceof PrimaryExpression) {
					statements.add(new Statement(item.expression, item.line));
				} else if (!((ConditionalExpression) item.expression).getElseBranch().isEmpty() && !((ConditionalExpression) item.expression).getThenBranch().isEmpty()) {
					IfThenElseStatement ifThenElseStatement = new IfThenElseStatement((ConditionalExpression)item.expression, item.line);
					List<Statement> thenStatements = ((ConditionalExpression) item.expression).getThenBranch().getStatements();
					BlockStatement thenBlock = new BlockStatement(item.line);
					for(Statement statement : thenStatements) {
						thenBlock.addStatement(statement);
					}
					List<Statement> elseStatements = ((ConditionalExpression) item.expression).getElseBranch().getStatements();
					BlockStatement elseBlock = new BlockStatement(item.line);
					for(Statement statement : elseStatements) {
						elseBlock.addStatement(statement);
					}
					ifThenElseStatement.setThenStatement(thenBlock);
					ifThenElseStatement.setElseStatement(elseBlock);
					statements.add(ifThenElseStatement);
				}
			} else if (!item.expression.isVirtual()) {
				statements.add(new Statement(item.expression, item.line));
			}
		}
		return statements;
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
