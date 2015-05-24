package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.expression.JumpExpression;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * A statement that represents a if-then statement
 */
public class IfThenStatement extends Statement {

	/**
	 * Expression printed in the brackets
	 */
	protected JumpExpression mCondition;

	/**
	 * Then block
	 */
	protected BlockStatement mThenBlock;

	/**
	 *
	 * @param condition a jump expression that represents a simple if-then branching
	 * @param line where the statement occurs in the original code
	 * @param parent owning statement or block
	 */
	public IfThenStatement(JumpExpression condition, int line, CodeElement parent){
		super(line, parent);
		mCondition = condition;
	}

	public void setThenBlock(BlockStatement thenBlock) {
		mThenBlock = thenBlock;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writeIfThenStatement(writer);
		writer.write(NL);
	}

	protected void writeIfThenStatement(Writer writer) throws IOException {
		StringWriter auxWriter = new StringWriter();
		mCondition.write(auxWriter);
		buf.setLength(0);
		buf.append("if (").append(auxWriter.toString()).append(")");
		writer.append(getTabs()).write(buf.toString());
		mThenBlock.write(writer);
	}
}
