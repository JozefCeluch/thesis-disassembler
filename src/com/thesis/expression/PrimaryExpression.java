package com.thesis.expression;

import com.thesis.Variable;
import com.thesis.common.DataType;
import com.thesis.common.Util;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.io.Writer;

public class PrimaryExpression extends Expression {

	private static final String QUOTE = "\"";

	private Object mValue;

	private PrimaryExpression(AbstractInsnNode node, Object value, DataType type) {
		super(node);
		mValue = value;
		mType = type;
	}

	public PrimaryExpression(LdcInsnNode node, Object constant, DataType type) {
		super(node);
		mValue = constant;
		mType = type;
	}

	public PrimaryExpression(FieldInsnNode node, Variable field, DataType type) {
		this(node, (Object) field, type);
	}

	public PrimaryExpression(VarInsnNode node, Variable variable, DataType type) {
		this(node, (Object) variable, type);
	}

	public PrimaryExpression(IincInsnNode node, int increment, DataType type) {
		this(node, (Object) increment, type);
	}

	public PrimaryExpression(IntInsnNode node, int operand, DataType type) {
		this(node, (Object) operand, type);
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
					mType = DataType.UNKNOWN;
					break;
				default:
					mValue = Integer.valueOf(val);
					mType = DataType.getType(Type.getType(opCode.substring(0, 1)));
			}
		}
	}

	public PrimaryExpression(Object value, DataType type) {
		this(null, value, type);
	}

	@Override
	public void setType(DataType type) {
		super.setType(type);
		if (mValue instanceof Variable) {
			((Variable)mValue).setType(type);
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		String output = mCastType != null ? "(" + mCastType.print() + ") " : "";

		if (mValue instanceof Variable) {
			output += mValue.toString();
		} else if (DataType.BOOLEAN.equals(mType)){
			output += (int)mValue == 0 ? "false" : "true";
		} else if (DataType.getType("java.lang.String").equals(mType)) {
			output += QUOTE + mValue + QUOTE;
		} else if (DataType.getType("java.lang.Class").equals(mType) && mValue instanceof DataType) {
			output += ((DataType)mValue).print() + ".class";
		} else {
			output += mValue.toString();
			if (DataType.FLOAT.equals(mType)) output += "F";
			if (DataType.LONG.equals(mType)) output += "L";
		}

		writer.write(output);
	}

	@Override
	public DataType getType() {
		return mType;
	}

	public Object getValue() {
		return mValue;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		// no preparation necessary
	}

	public boolean isConstant() {
		return !(mInstruction instanceof VarInsnNode || mInstruction instanceof FieldInsnNode);
	}
}
