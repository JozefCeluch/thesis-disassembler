package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.translator.StatementCreator;
import com.thesis.expression.JumpExpression;
import com.thesis.expression.UnconditionalJump;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * A statement that represents a do-while loop
 * <p>
 * Created from a general {@link JumpExpression} that fits do-while loop pattern
 */
public class DoWhileLoopStatement extends Statement {

	/**
	 * Inside of the loop
	 */
	private BlockStatement mBlock;

	/**
	 * @param expression jump expression that represents a do-while loop
	 * @param line where the expression occurred in the original code
	 * @param parent owning statement or block
	 */
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
