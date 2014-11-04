package com.thesis.block;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class BlockStatement extends Statement {

	private List<Statement> mStatements;

	public BlockStatement(){
		mStatements = new ArrayList<>();
	}

	public void addStatement(Statement statement) {
		mStatements.add(statement);
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write(BLOCK_START);
		for(Statement statement : mStatements) {
			statement.write(writer);
		}
		writer.write(CLOSING_BRACKET);
	}
}
