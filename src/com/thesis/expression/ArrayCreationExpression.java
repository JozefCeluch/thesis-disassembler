package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.tree.IntInsnNode;

import java.io.IOException;
import java.io.Writer;

public class ArrayCreationExpression extends Expression {

	private Expression mLength;

	public ArrayCreationExpression(IntInsnNode node) {
		super(node);
		mType = convertTypeCodeToType(node.operand);
	}

	public void setLength(Expression length) {
		mLength = length;
	}

	@Override
	public DataType getType() {
		return mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		mLength = stack.pop();
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("new ");
		writer.write(mType.toString());
		writer.write("[");
		mLength.write(writer);
		writer.write("]");
	}

	private DataType convertTypeCodeToType(int code) {
		switch (code) {
			case 4:
				return DataType.BOOLEAN;
			case 5:
				return DataType.CHAR;
			case 6:
				return DataType.FLOAT;
			case 7:
				return DataType.DOUBLE;
			case 8:
				return DataType.BYTE;
			case 9:
				return DataType.SHORT;
			case 10:
				return DataType.INT;
			case 11:
				return DataType.LONG;
		}
		return null;
	}
}
