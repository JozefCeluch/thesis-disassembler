package com.thesis.expression;

import com.thesis.LocalVariable;
import com.thesis.common.DataType;
import com.thesis.common.Util;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;

import java.io.IOException;
import java.io.Writer;

public class PrimaryExpression extends Expression {

	private static final String QUOTE = "\"";

	private Object mValue;

	public PrimaryExpression(AbstractInsnNode node, Object value, DataType type) {
		super(node);
		if (DataType.getType("String").equals(type) || DataType.getType("java.lang.String").equals(type)) {
			mValue = QUOTE + value + QUOTE;
		} else {
			mValue = value;
		}
		mType = type;
	}

	public PrimaryExpression(AbstractInsnNode node, LocalVariable value, DataType type) {
		super(node);
		mValue = value;
		mType = type;
	}

	public PrimaryExpression(InsnNode instruction) {
		super(instruction);
		String opCode = Util.getOpcodeString(instruction.getOpcode());

		if (opCode.contains("CONST")) {
			int valPos = opCode.lastIndexOf("_");
			String val = opCode.substring(valPos + 1);
			switch (val) {
				case "M1":
					mValue = -1;
					mType = DataType.INT;
					break;
				case "NULL":
					mValue = "null";
					mType = DataType.getType("java.lang.Object");
					break;
				default:
					mValue = Integer.valueOf(val);
					mType = Util.getType(opCode.substring(0, 1));
			}
		}
	}

	@Override
	public void setType(DataType type) {
		super.setType(type);
		if (mValue instanceof LocalVariable) {
			((LocalVariable) mValue).setType(type);
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		String output = mCastType != null ? "(" + mCastType.toString() + ") " : "";

		if (mValue instanceof LocalVariable) {
			output += ((LocalVariable)mValue).getName();
		} else if (DataType.BOOLEAN.equals(mType)){
			output += (int)mValue == 0 ? "false" : "true";
		} else {
			output += mValue.toString();
		}

		writer.write(output);
	}

	@Override
	public DataType getType() {
		return mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		// no preparation necessary
	}

	public static void createAndAdd(InsnNode instruction, ExpressionStack stack) {
		stack.push(new PrimaryExpression(instruction));
	}
}
