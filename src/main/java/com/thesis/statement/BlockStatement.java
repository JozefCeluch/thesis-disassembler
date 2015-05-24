package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.expression.Expression;
import com.thesis.translator.ExpressionStack;
import com.thesis.translator.StatementCreator;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * A statement that represents a Java code block, enclosed in curly brackets
 */
public class BlockStatement extends Statement {

	/**
	 * List of statements contained in the block
	 */
	private List<Statement> mStatements;

	/**
	 * Creates a new block with the provided statements
	 * @param line where the statement occurs in the original code
	 * @param statements list of statements for this block
	 * @param parent owning statement
	 */
	public BlockStatement(int line, List<Statement> statements, CodeElement parent) {
		super(line, parent);
		mStatements = statements;
	}

	/**
	 * Creates a new block from the provided {@link ExpressionStack}
	 * @param line where the statement occurs in the original code
	 * @param stack ExpressionStack that is to be converted to list of statements
	 * @param parent owning statement
	 */
	public BlockStatement(int line, ExpressionStack stack, CodeElement parent){
		super(line, parent);
		mStatements = new StatementCreator(stack, parent).getStatements();
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(" {" + NL);
		for(Statement statement : mStatements) {
			statement.write(writer);
		}
		writer.append(getTabs().replaceFirst(TAB, "")).append("}");
	}
}
