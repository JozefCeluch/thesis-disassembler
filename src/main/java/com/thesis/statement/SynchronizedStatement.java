package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.expression.MonitorExpression;

import java.io.IOException;
import java.io.Writer;

/**
 * A statement that represents the synchronized block
 */
public class SynchronizedStatement extends Statement {

	private BlockStatement mBlockStatement;

	/**
	 *
	 * @param expression monitor expression
	 * @param line where the statement occurs in the original code
	 * @param parent owning statement or block
	 */
	public SynchronizedStatement(MonitorExpression expression, int line, CodeElement parent) {
		super(expression, line, parent);
		mBlockStatement = new BlockStatement(line, expression.getSynchronizedBlock(), this);
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.append(getTabs()).write("synchronized (");
		mExpression.write(writer);
		writer.write(")");
		mBlockStatement.write(writer);
		writer.write(NL);
	}
}
