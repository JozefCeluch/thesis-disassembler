package com.thesis.expression;

import java.io.IOException;
import java.io.Writer;

public class ArrayCreationExpression extends Expression {

	private Expression mLength;

	public ArrayCreationExpression(Expression length, int typeCode) {
		mLength = length;
		mType = convertTypeCodeToString(typeCode);
	}

	public void setType(String type) {
	}

	@Override
	public String getType() {
		return mType;
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("new ");
		writer.write(mType);
		writer.write("[");
		mLength.write(writer);
		writer.write("]");
	}

	private String convertTypeCodeToString(int code) {
		switch (code) {
			case 4:
				return "boolean";
			case 5:
				return "char";
			case 6:
				return "float";
			case 7:
				return "double";
			case 8:
				return "byte";
			case 9:
				return "short";
			case 10:
				return "int";
			case 11:
				return "long";
		}
		return null;
	}
}
