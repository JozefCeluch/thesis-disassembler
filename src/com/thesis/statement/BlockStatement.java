package com.thesis.statement;

import com.thesis.common.CodeElement;
import com.thesis.expression.Expression;
import com.thesis.translator.ExpressionStack;
import com.thesis.translator.StatementCreator;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class BlockStatement extends Statement {

	private List<Statement> mStatements;

	public BlockStatement(int line, List<Statement> statements, CodeElement parent) {
		super(line, parent);
		mStatements = statements;
		for (Statement s : mStatements) {
			s.setParent(this);
		}
	}

	public BlockStatement(int line, ExpressionStack stack, CodeElement parent){
		super(line, parent);
		mStatements = new StatementCreator(stack, this).getStatements();
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(" {" + NL);
		for(Statement statement : mStatements) {
			statement.write(writer);
		}
		writer.append(getTabs()).append("}");
	}
}
