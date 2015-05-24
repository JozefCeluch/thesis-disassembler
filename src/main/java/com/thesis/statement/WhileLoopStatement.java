package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.expression.JumpExpression;
import com.thesis.expression.UnconditionalJump;

import java.io.IOException;
import java.io.Writer;

/**
 * A statement that represents the while loop
 * <p>
 * Created from a general {@link JumpExpression} that fits while loop pattern
 */
public class WhileLoopStatement extends Statement {

	private BlockStatement mBlock;

	/**
	 * @param expression jump expression that represents a while loop
	 * @param line where the expression occurred in the original code
	 * @param parent owning statement or block
	 */
	public WhileLoopStatement(JumpExpression expression, int line, CodeElement parent) {
		super(expression, line, parent);
		mBlock = new BlockStatement(line, expression.getThenBranch(), this);
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
