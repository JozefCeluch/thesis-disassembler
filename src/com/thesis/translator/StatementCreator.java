package com.thesis.translator;

import com.thesis.common.CodeElement;
import com.thesis.common.DataType;
import com.thesis.expression.*;
import com.thesis.statement.*;

import java.util.ArrayList;
import java.util.List;

public class StatementCreator {

	private ExpressionStack mStack;
	private List<Statement> mStatements;
	private CodeElement mParent;

	public StatementCreator(ExpressionStack stack, CodeElement parent) {
		mStack = stack;
		mParent = parent;
	}

	private void createStatements(){
		mStatements = createStatements(mStack);
	}

	private List<Statement> createStatements(ExpressionStack expressions) {
		List<Statement> statements = new ArrayList<>();
		if (expressions == null) return statements;
		for (ExpressionStack.Item item : expressions.getAll()) {
			if (item.getExpression().isVirtual()) continue;

			if (item.getExpression() instanceof JumpExpression) {
				statements.add(handleConditionalExpression((JumpExpression) item.getExpression(), item.getLine(), item.getLabelId()));
			} else if (item.getExpression() instanceof SwitchExpression) {
				statements.add(handleSwitchExpression((SwitchExpression) item.getExpression(), item.getLine(), item.getLabelId()));
			} else if (item.getExpression() instanceof TryCatchExpression) {
				statements.add(new TryCatchStatement((TryCatchExpression)item.getExpression(), item.getLine(), mParent));
			} else if (item.getExpression() instanceof MonitorExpression) {
				statements.add(new SynchronizedStatement((MonitorExpression)item.getExpression(), item.getLine(), mParent));
			} else {
				statements.add(new Statement(item.getExpression(), item.getLine(), mParent));
			}
		}
		return statements;
	}

	private Statement handleSwitchExpression(SwitchExpression expression, int line, int labelId) {
		SwitchStatement statement = new SwitchStatement(expression, line, mParent);
		List<Statement> caseStatements = new ArrayList<>();
		for(SwitchExpression.CaseExpression caseExp : expression.getCaseList()) {
			caseStatements.add(new SwitchStatement.CaseStatement(caseExp, line, createStatements(caseExp.getStack()), statement)); //TODO line
		}
		statement.setSwitchBlock(new BlockStatement(line, caseStatements, statement));
		return statement;
	}

	private Statement handleConditionalExpression(JumpExpression expression, int line, int label) {
		if (expression.isLoop()) {
			switch (expression.getLoopType()) {
				case NONE:
					break;
				case WHILE:
					return new WhileLoopStatement(expression, line, mParent);
				case FOR:
					break;
				case DO_WHILE:
					return new DoWhileLoopStatement(expression, line, mParent);
			}
		}
		if (isIfThenElseStatement(expression)) {
			IfThenElseStatement ifThenElseStatement = new IfThenElseStatement(expression, line, mParent);
			List<Statement> thenStatements = createStatements(expression.getThenBranch());
			List<Statement> elseStatements = createStatements(expression.getElseBranch());

			ifThenElseStatement.setThenStatement(new BlockStatement(line, thenStatements, ifThenElseStatement));
			ifThenElseStatement.setElseStatement(new BlockStatement(line, elseStatements, ifThenElseStatement));
			return ifThenElseStatement;
		}
		if (isIfThenStatement(expression)){
			IfThenStatement ifThenStatement = new IfThenStatement(expression, line, mParent);
			List<Statement> thenStatements = createStatements(expression.getThenBranch());
			ifThenStatement.setThenStatement(new BlockStatement(line, thenStatements, ifThenStatement));
			return ifThenStatement;
		}
		if (expression instanceof UnconditionalJump) {
			return new Statement(expression, line, mParent);
		}
		//TODO throw exception
		return new Statement(new PrimaryExpression(0, "UNKNOWN CONDITIONAL EXPRESSION ", DataType.getTypeFromObject("java.lang.String")),0, mParent);
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
