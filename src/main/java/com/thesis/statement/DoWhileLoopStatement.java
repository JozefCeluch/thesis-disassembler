package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.translator.StatementCreator;
import com.thesis.expression.JumpExpression;
import com.thesis.expression.UnconditionalJump;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class DoWhileLoopStatement extends Statement {

	private BlockStatement mBlock;

	public DoWhileLoopStatement(JumpExpression expression, int line, CodeElement parent) {
		super(expression, line, parent);
		mBlock = new BlockStatement(line, expression.getThenBranch(), this);
	}

	@Override
	public void write(Writer writer) throws IOException {
		String tabs = getTabs();
		writer.append(tabs).write("do");
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
