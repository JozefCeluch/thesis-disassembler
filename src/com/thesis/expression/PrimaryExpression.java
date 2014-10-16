package com.thesis.expression;

import com.thesis.LocalVariable;
import com.thesis.common.Util;
import org.objectweb.asm.tree.InsnNode;

import java.io.IOException;
import java.io.Writer;

public class PrimaryExpression extends Expression {

	private static final String QUOTE = "\"";

	private Object mValue;

	public PrimaryExpression(Object value, String type) {
		mValue = value;
		mType = type;
	}

	public PrimaryExpression(String value, String type) {
		mValue = QUOTE + value + QUOTE;
		mType = type;
	}

	public PrimaryExpression(LocalVariable value, String type) {
		super();
		mValue = value;
		value.setType(type);
		mType = type;
	}

	public PrimaryExpression(InsnNode instruction) {
		String opCode = Util.getOpcodeString(instruction.getOpcode());
		if (opCode.contains("CONST")) {
			int valPos = opCode.lastIndexOf("_");
			String val = opCode.substring(valPos + 1);
			switch (val) {
				case "M1":
					mValue = -1;
					mType = "int";
					break;
				case "NULL":
					mValue = "null";
					mType = "java.lang.Object";
					break;
				default:
					mValue = Integer.valueOf(val);
					mType = Util.getPrimitiveType(opCode.substring(0,1));
			}
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		String output = mValue.toString();
		if (mValue instanceof LocalVariable) {
			output = ((LocalVariable)mValue).getName();
		}
		writer.write(output);
	}

	@Override
	public String getType() {
		return mType;
	}
}
