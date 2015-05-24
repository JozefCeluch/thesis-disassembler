package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.expression.Expression;

import java.io.IOException;
import java.io.Writer;

/**
 * A general representation of a single Java statement
 * <p>
 * A general Statement encloses an {@link Expression} that can be written out as a Java statement.
 * For the simple expressions, this class adds only the correct indentation and a semicolon at the end of line.
 * Statements for more complex expressions add a bit more formatting.
 */
public class Statement extends CodeElement {

	/**
	 * New line
	 */
	protected static final String NL = "\n";

	/**
	 * Semicolon followed by new line
	 */
	protected static final String STATEMENT_END_NL = ";\n";

	/**
	 * Semicolon
	 */
	protected static final String STATEMENT_END = "; ";

	/**
	 * Expression enclosed in the statement
	 */
	protected Expression mExpression;

	/**
	 * Line of the expression
	 */
	protected int mLine;

	/**
	 * Flag if the statement should be printed with a new line at the end
	 */
	protected boolean mAddNewLine;

	/**
	 * Flag if the statement should write the statement end
	 */
	protected boolean mWriteEnd;
	protected StringBuffer buf;

	protected Statement(int line, CodeElement parent) {
		super(parent);
		mLine = line;
		buf = new StringBuffer();
	}

	public Statement(Expression expression, int line, CodeElement parent) {
		this(line, parent);
		mExpression = expression;
		mAddNewLine = true;
		mWriteEnd = true;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(getTabs());
		mExpression.write(writer);
		if (mWriteEnd) {
			writeEnd(writer);
		}
	}

	protected void writeEnd(Writer writer) throws IOException{
		writer.write(mAddNewLine ? STATEMENT_END_NL : STATEMENT_END);
	}
}
