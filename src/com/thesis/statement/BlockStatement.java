package com.thesis.statement;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class BlockStatement extends Statement {

	private List<Statement> mStatements;

	public BlockStatement(int line, List<Statement> statements){
		super(line);
		mStatements = statements;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(" {" + NL);
		for(Statement statement : mStatements) {
			statement.write(writer);
		}
		writer.write("}");
	}
}
