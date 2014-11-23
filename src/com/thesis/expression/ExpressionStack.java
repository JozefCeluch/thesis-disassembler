package com.thesis.expression;

import com.thesis.InstructionTranslator;
import com.thesis.block.BlockStatement;
import com.thesis.block.IfThenElseStatement;
import com.thesis.block.Statement;
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
		mLabels = InstructionTranslator.getLabels();
	}

	public void push(Expression expression) {
		expression.prepareForStack(this);
		pushCompleteExp(expression);
		improveStack();
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
				mLastImprovementPosition = i;
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

	public void addAll(ExpressionStack stack){
		if (stack == null) return;
		for(StackItem exp : stack.getAll()){
			mStack.push(exp);
		}
	}

	private List<StackItem> getAll() {
		return Arrays.asList(mStack.toArray(new StackItem[mStack.size()]));
	}

	public void clear() {
		mStack.clear();
	}

	private void pushCompleteExp(Expression expression) {
		mStack.push(new StackItem(expression, mLabel, mLineNum));
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

	public List<Statement> getStatements() {
		List<Statement> statements = new ArrayList<>();
		for (StackItem item : mStack) {
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

}
