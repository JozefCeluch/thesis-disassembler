package com.thesis.block;

import com.thesis.StatementCreator;
import com.thesis.expression.ConditionalExpression;
import com.thesis.expression.Expression;
import com.thesis.expression.UnconditionalJump;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class WhileLoopStatement extends Statement {

	private BlockStatement mBlock;

	public WhileLoopStatement(ConditionalExpression expression, int line) {
		super(expression, line);
		List<Statement> statements = new StatementCreator(expression.getThenBranch()).getStatements();
		mBlock = new BlockStatement(line, statements);
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("while (");
		if (mExpression instanceof UnconditionalJump) {
			writer.write("true");
		} else {
			mExpression.write(writer);
		}
		writer.write(")");
		mBlock.write(writer);
		writer.write(NL);
	}
}
