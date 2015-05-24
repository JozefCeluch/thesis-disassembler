package com.thesis.translator;

import com.thesis.common.CodeElement;
import com.thesis.common.DataType;
import com.thesis.expression.*;
import com.thesis.statement.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts the expression stack to the list of statements
 */
public class StatementCreator {

	private ExpressionStack mStack;
	private List<Statement> mStatements;
	private CodeElement mParent;

	/**
	 * Creates a new instance
	 * @param stack stack that is being converted to statements
	 * @param parent parent of the stack
	 */
	public StatementCreator(ExpressionStack stack, CodeElement parent) {
		mStack = stack;
		mParent = parent;
	}

	/**
	 * @return list of converted statements
	 */
	public List<Statement> getStatements() {
		if (mStatements == null) {
			createStatements();
		}
		return mStatements;
	}

	private void createStatements(){
		mStatements = createStatements(mStack, mParent);
	}

	private List<Statement> createStatements(ExpressionStack expressions, CodeElement parent) {
		List<Statement> statements = new ArrayList<>();
		if (expressions == null) return statements;
		for (int i = 0; i < expressions.size(); i++) {
			Expression exp = expressions.get(i);
			if (exp.isVirtual()) continue;

			if (exp instanceof JumpExpression) {
				statements.add(handleConditionalExpression((JumpExpression) exp, exp.getLine(), parent));
			} else if (exp instanceof SwitchExpression) {
				statements.add(handleSwitchExpression((SwitchExpression) exp, exp.getLine(), parent));
			} else if (exp instanceof TryCatchExpression) {
				statements.add(new TryCatchStatement((TryCatchExpression) exp, exp.getLine(), parent));
			} else if (exp instanceof MonitorExpression) {
				statements.add(new SynchronizedStatement((MonitorExpression) exp, exp.getLine(), parent));
			} else {
				statements.add(new Statement(exp, exp.getLine(), parent));
			}
		}
		return statements;
	}

	private Statement handleSwitchExpression(SwitchExpression expression, int line, CodeElement parent) {
		SwitchStatement statement = new SwitchStatement(expression, line, parent);
		List<Statement> caseStatements = new ArrayList<>();
		for(SwitchExpression.CaseExpression caseExp : expression.getCaseList()) {
			caseStatements.add(new SwitchStatement.CaseStatement(caseExp, line, createStatements(caseExp.getStack(), statement), statement)); //TODO line
		}
		statement.setSwitchBlock(new BlockStatement(line, caseStatements, statement));
		return statement;
	}

	private Statement handleConditionalExpression(JumpExpression expression, int line, CodeElement parent) {
		if (expression.isLoop()) {
			switch (expression.getLoopType()) {
				case NONE:
					break;
				case WHILE:
					return new WhileLoopStatement(expression, line, parent);
				case FOR:
					break;
				case DO_WHILE:
					return new DoWhileLoopStatement(expression, line, parent);
			}
		}
		if (isIfThenElseStatement(expression)) {
			IfThenElseStatement ifThenElseStatement = new IfThenElseStatement(expression, line, parent);
			List<Statement> thenStatements = createStatements(expression.getThenBranch(), ifThenElseStatement);
			List<Statement> elseStatements = createStatements(expression.getElseBranch(), ifThenElseStatement);

			ifThenElseStatement.setThenBlock(new BlockStatement(line, thenStatements, ifThenElseStatement));
			ifThenElseStatement.setElseBlock(new BlockStatement(line, elseStatements, ifThenElseStatement));
			return ifThenElseStatement;
		}
		if (isIfThenStatement(expression)){
			IfThenStatement ifThenStatement = new IfThenStatement(expression, line, parent);
			List<Statement> thenStatements = createStatements(expression.getThenBranch(), ifThenStatement);
			ifThenStatement.setThenBlock(new BlockStatement(line, thenStatements, ifThenStatement));
			return ifThenStatement;
		}
		if (expression instanceof UnconditionalJump) {
			return new Statement(expression, line, parent);
		}
		//TODO throw exception?
		return new Statement(new PrimaryExpression(0, "UNKNOWN CONDITIONAL EXPRESSION ", DataType.getTypeFromObject("java.lang.String")),0, parent);
	}

	private boolean isIfThenStatement(JumpExpression expression) {
		return expression.getThenBranch() != null && !expression.getThenBranch().isEmpty()
				&& (expression.getElseBranch() == null || expression.getElseBranch().isEmpty()) && !expression.isLoop();
	}

	private boolean isIfThenElseStatement(JumpExpression expression) {
		return expression.getThenBranch() != null && !expression.getThenBranch().isEmpty()
				&& expression.getElseBranch() != null && !expression.getElseBranch().isEmpty();
	}
}
