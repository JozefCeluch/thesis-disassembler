package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.expression.JumpExpression;

import java.io.IOException;
import java.io.Writer;

/**
 * A statement that represents a if-then-else statement
 * <p>
 * An extension of if-then statement
 */
public class IfThenElseStatement extends IfThenStatement {

	private BlockStatement mElseBlock;

	/**
	 *
	 * @param condition jump expression that has an else block
	 * @param line where the statement occurs in the original code
	 * @param parent owning statement or block
	 */
	public IfThenElseStatement(JumpExpression condition, int line, CodeElement parent){
		super(condition, line, parent);
	}

	public void setElseBlock(BlockStatement elseBlock) {
		mElseBlock = elseBlock;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writeIfThenStatement(writer);
		writer.write(" else");
		mElseBlock.write(writer);
		writer.write("\n");
	}
}
