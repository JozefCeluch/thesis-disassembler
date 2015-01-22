package com.thesis;

import com.thesis.block.*;
import com.thesis.common.DataType;
import com.thesis.expression.*;

import java.util.ArrayList;
import java.util.List;

public class StatementCreator {

	private ExpressionStack mStack;
	private List<Statement> mStatements;

	public StatementCreator(ExpressionStack stack) {
		mStack = stack;
	}

	private void createStatements(){
		mStatements = createStatements(mStack);
	}

	private List<Statement> createStatements(ExpressionStack expressions) {
		List<Statement> statements = new ArrayList<>();
		for (StackItem item : expressions.getAll()) {
			if (item.expression.isVirtual() || item.expression instanceof PrimaryExpression) continue;

			if (item.expression instanceof ConditionalExpression) {
				statements.add(handleConditionalExpression((ConditionalExpression) item.expression, item.line, item.labelId));
			} else if (item.expression instanceof SwitchExpression) {
				statements.add(handleSwitchExpression((SwitchExpression) item.expression, item.line, item.labelId));
			} else if (item.expression instanceof TryCatchExpression) {
				statements.add(new TryCatchStatement((TryCatchExpression)item.expression, item.line));
			} else {
				statements.add(new Statement(item.expression, item.line));
			}
		}
		return statements;
	}

	private Statement handleSwitchExpression(SwitchExpression expression, int line, int labelId) {
		SwitchStatement statement = new SwitchStatement(expression, line);
		List<Statement> caseStatements = new ArrayList<>();
		for(SwitchExpression.CaseExpression caseExp : expression.getCaseList()) {
			caseStatements.add(new SwitchStatement.CaseStatement(caseExp, line, createStatements(caseExp.getStack()))); //TODO line
		}
		statement.setSwitchBlock(new BlockStatement(line, caseStatements));
		return statement;
	}

	private Statement handleConditionalExpression(ConditionalExpression expression, int line, int label) {
		if (isIfThenElseStatement(expression)) {
			IfThenElseStatement ifThenElseStatement = new IfThenElseStatement(expression, line);
			List<Statement> thenStatements = createStatements(expression.getThenBranch());
			List<Statement> elseStatements = createStatements(expression.getElseBranch());

			ifThenElseStatement.setThenStatement(new BlockStatement(line, thenStatements));
			ifThenElseStatement.setElseStatement(new BlockStatement(line, elseStatements));
			return ifThenElseStatement;
		} else if (isIfThenStatement(expression)){
			IfThenStatement ifThenStatement = new IfThenStatement(expression, line);
			List<Statement> thenStatements = createStatements(expression.getThenBranch());
			ifThenStatement.setThenStatement(new BlockStatement(line, thenStatements));
			return ifThenStatement;
		} else if (expression instanceof JumpExpression) {
			return new Statement(expression, line);
		}
		//TODO loops
		return new Statement(new PrimaryExpression(null, "CONDITIONAL EXPRESSION ", DataType.getType("String")),0);
	}

	private boolean isIfThenStatement(ConditionalExpression expression) {
		return !expression.getThenBranch().isEmpty() && expression.getElseBranch().isEmpty();
	}

	private boolean isIfThenElseStatement(ConditionalExpression expression) {
		return !expression.getThenBranch().isEmpty() && !expression.getElseBranch().isEmpty();
	}

	public List<Statement> getStatements() {
		if (mStatements == null) {
			createStatements();
		}
		return mStatements;
	}
}
