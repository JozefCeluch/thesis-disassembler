package com.thesis.statement;

import com.thesis.translator.StatementCreator;
import com.thesis.expression.JumpExpression;
import com.thesis.expression.UnconditionalJump;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class DoWhileLoopStatement extends Statement {

	private BlockStatement mBlock;

	public DoWhileLoopStatement(JumpExpression expression, int line) {
		super(expression, line);
		List<Statement> statements = new StatementCreator(expression.getThenBranch()).getStatements();
		mBlock = new BlockStatement(line, statements);
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("do");
		mBlock.write(writer);
		writer.write(" while (");
		if (mExpression instanceof UnconditionalJump) {
			writer.write("true");
		} else {
			mExpression.write(writer);
		}
		writer.write(")");
		writer.write(NL);
	}
}
