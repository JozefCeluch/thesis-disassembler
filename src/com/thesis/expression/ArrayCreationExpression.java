package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class ArrayCreationExpression extends Expression {

	private Expression mLength;
	private List<Expression> mItems;

	protected ArrayCreationExpression(AbstractInsnNode node, DataType type) {
		super(node);
		mType = type;
		mItems = new ArrayList<>();
	}

	public ArrayCreationExpression(IntInsnNode node) {
		this(node, convertTypeCodeToType(node.operand));
	}

	public ArrayCreationExpression(TypeInsnNode node) {
		this(node, DataType.getType(Util.javaObjectName(node.desc)));
	}

	public void setLength(Expression length) {
		mLength = length;
	}

	public void addMember(Expression expression) {
		mItems.add(expression);
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
		if (mItems.isEmpty()) mLength.write(writer);
		writer.write("]");
		if (!mItems.isEmpty()) {
			writer.write('{');
			for(int i = 0; i < mItems.size(); i++) {
				writer.write(Util.getCommaIfNeeded(i));
				mItems.get(i).write(writer);
			}
			writer.write('}');
		}
	}

	private static DataType convertTypeCodeToType(int code) {
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
