package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.translator.StatementCreator;
import com.thesis.expression.JumpExpression;
import com.thesis.expression.UnconditionalJump;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class WhileLoopStatement extends Statement {

	private BlockStatement mBlock;

	public WhileLoopStatement(JumpExpression expression, int line, CodeElement parent) {
		super(expression, line, parent);
		mBlock = new BlockStatement(line, expression.getThenBranch(), parent);
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.append(getTabs()).write("while (");
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
