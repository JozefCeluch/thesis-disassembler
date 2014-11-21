package com.thesis.expression;

import com.thesis.LocalVariable;
import com.thesis.common.Util;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;

import java.io.IOException;
import java.io.Writer;

public class PrimaryExpression extends Expression {

	private static final String QUOTE = "\"";

	private Object mValue;

	public PrimaryExpression(AbstractInsnNode node, Object value, String type) {
		super(node);
		if ("String".equals(type) || "java.lang.String".equals(type)) {
			mValue = QUOTE + value + QUOTE;
		} else {
			mValue = value;
		}
		mType = type;
	}

	public PrimaryExpression(AbstractInsnNode node, LocalVariable value, String type) {
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
		} else if ("boolean".equals(mType)){
			output = (int)mValue == 0 ? "false" : "true";
		}

		writer.write(output);
	}

	@Override
	public String getType() {
		return mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		// no preparation necessary
	}
}
