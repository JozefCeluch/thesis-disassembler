package com.thesis.translator;

import com.thesis.common.DataType;
import com.thesis.expression.*;
import com.thesis.statement.*;

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
		for (ExpressionStack.Item item : expressions.getAll()) {
			if (item.getExpression().isVirtual()) continue;

			if (item.getExpression() instanceof JumpExpression) {
				statements.add(handleConditionalExpression((JumpExpression) item.getExpression(), item.getLine(), item.getLabelId()));
			} else if (item.getExpression() instanceof SwitchExpression) {
				statements.add(handleSwitchExpression((SwitchExpression) item.getExpression(), item.getLine(), item.getLabelId()));
			} else if (item.getExpression() instanceof TryCatchExpression) {
				statements.add(new TryCatchStatement((TryCatchExpression)item.getExpression(), item.getLine()));
			} else if (item.getExpression() instanceof MonitorExpression) {
				statements.add(new SynchronizedStatement((MonitorExpression)item.getExpression(), item.getLine()));
			} else {
				statements.add(new Statement(item.getExpression(), item.getLine()));
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

	private Statement handleConditionalExpression(JumpExpression expression, int line, int label) {
		if (expression.isLoop()) {
			switch (expression.getLoopType()) {
				case NONE:
					break;
				case WHILE:
					return new WhileLoopStatement(expression, line);
				case FOR:
					break;
				case DO_WHILE:
					return new DoWhileLoopStatement(expression, line);
			}
		}
		if (isIfThenElseStatement(expression)) {
			IfThenElseStatement ifThenElseStatement = new IfThenElseStatement(expression, line);
			List<Statement> thenStatements = createStatements(expression.getThenBranch());
			List<Statement> elseStatements = createStatements(expression.getElseBranch());

			ifThenElseStatement.setThenStatement(new BlockStatement(line, thenStatements));
			ifThenElseStatement.setElseStatement(new BlockStatement(line, elseStatements));
			return ifThenElseStatement;
		}
		if (isIfThenStatement(expression)){
			IfThenStatement ifThenStatement = new IfThenStatement(expression, line);
			List<Statement> thenStatements = createStatements(expression.getThenBranch());
			ifThenStatement.setThenStatement(new BlockStatement(line, thenStatements));
			return ifThenStatement;
		}
		if (expression instanceof UnconditionalJump) {
			return new Statement(expression, line);
		}
		//TODO throw exception
		return new Statement(new PrimaryExpression(0, "UNKNOWN CONDITIONAL EXPRESSION ", DataType.getTypeFromObject("java.lang.String")),0);
	}

	private boolean isIfThenStatement(JumpExpression expression) {
		return expression.getThenBranch() != null && !expression.getThenBranch().isEmpty()
				&& (expression.getElseBranch() == null || expression.getElseBranch().isEmpty()) && !expression.isLoop();
	}

	private boolean isIfThenElseStatement(JumpExpression expression) {
		return expression.getThenBranch() != null && !expression.getThenBranch().isEmpty()
				&& expression.getElseBranch() != null && !expression.getElseBranch().isEmpty();
	}

	public List<Statement> getStatements() {
		if (mStatements == null) {
			createStatements();
		}
		return mStatements;
	}
}
