package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import org.objectweb.asm.Opcodes;

public class ConstantPrimaryExpression extends PrimaryExpression {
	public ConstantPrimaryExpression(int opCode) {
		super(opCode, getValue(opCode), getType(opCode));
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	private static Object getValue(int opCode) {
		String opCodeStr = Util.getOpcodeString(opCode);

		if (opCodeStr.contains("CONST")) {
			int valPos = opCodeStr.lastIndexOf("_");
			String val = opCodeStr.substring(valPos + 1);
			switch (val) {
				case "M1":
					return -1;
				case "NULL":
					return "null";
				default:
					return Integer.valueOf(val);
			}
		}

		throw new RuntimeException("Unexpected opCode");
	}

	private static DataType getType(int opCode) {
		if (Util.isBetween(opCode, Opcodes.ICONST_M1, Opcodes.ICONST_5)) {
			return DataType.INT;
		} else if (Util.isBetween(opCode, Opcodes.FCONST_0, Opcodes.FCONST_2)) {
			return DataType.FLOAT;
		} else if (Util.isBetween(opCode, Opcodes.DCONST_0, Opcodes.DCONST_1)) {
			return DataType.DOUBLE;
		} else if (Util.isBetween(opCode, Opcodes.LCONST_0, Opcodes.LCONST_1)) {
			return DataType.LONG;
		} else if (opCode == Opcodes.ACONST_NULL) {
			return DataType.UNKNOWN;
		}
		throw new RuntimeException("Unexpected opCode");
	}
}
