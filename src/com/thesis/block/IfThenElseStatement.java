package com.thesis.block;

import com.thesis.expression.Expression;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class IfThenElseStatement extends Block {

	Expression mCondition;
	Statement mThenStatement;
	Statement mElseStatement;

	@Override
	public Block disassemble() {
		return this;
	}

	@Override
	public void write(Writer writer) throws IOException {
		StringWriter auxWriter = new StringWriter();
		mCondition.write(auxWriter);
		buf.setLength(0);
		buf.append("if (").append(auxWriter.toString()).append(") then");
		writer.write(buf.toString());
		mThenStatement.write(writer);
		if (mElseStatement != null) {
			writer.write("else");
			mElseStatement.write(writer);
		}
	}
}
