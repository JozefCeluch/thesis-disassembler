package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class ArrayCreationExpression extends Expression {

	private List<Expression> mLengths;
	private List<Expression> mItems;
	private int mDimensions;

	protected ArrayCreationExpression(AbstractInsnNode node, DataType type) {
		super(node);
		mType = type;
		mItems = new ArrayList<>();
		mLengths = new ArrayList<>();
		mDimensions = 1;
	}

	public ArrayCreationExpression(IntInsnNode node) {
		this(node, convertTypeCodeToType(node.operand));
	}

	public ArrayCreationExpression(TypeInsnNode node) {
		this(node, convertTypeStringToType(node.desc));
		mDimensions = mType.getDimension() + 1;
	}

	public ArrayCreationExpression(MultiANewArrayInsnNode node) {
		this(node, convertTypeStringToType(node.desc));
		mDimensions = node.dims;
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
		if (mInstruction.getOpcode() == Opcodes.MULTIANEWARRAY) {
			for (int i = 0; i < mDimensions; i++) {
				mLengths.add(0, stack.pop());
			}
		} else {
			mLengths.add(stack.pop());
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("new ");
		writer.write(mType.toString());
		for (int i = 0; i < mDimensions; i++) {
			writer.write("[");
			if (mItems.isEmpty() && i < mLengths.size()) mLengths.get(i).write(writer);
			writer.write("]");
		}
		if (!mItems.isEmpty()) {
			writer.write('{');
			for(int i = 0; i < mItems.size(); i++) {
				writer.write(Util.getCommaIfNeeded(i));
				mItems.get(i).write(writer);
			}
			writer.write('}');
		}
	}

	private static DataType convertTypeStringToType(String desc) {
		if (desc == null || desc.isEmpty()) {
			return DataType.UNKNOWN;
		}
		return DataType.getType(Type.getObjectType(desc));
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
