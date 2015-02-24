package com.thesis.common;

import com.thesis.file.Parser;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.util.Printer;

import java.util.List;

public class Util {

	public static final String ARGUMENT_NAME_BASE = "arg";

	public static DataType getType(String desc){
		DataType type;
		if (desc.startsWith("L")) {
			type = getReferenceType(desc);
		}else if (desc.startsWith("[")) {
			type = getArrayReferenceType(desc);
		} else {
			type = getPrimitiveType(desc);
		}
		return type;
	}

	private static DataType getArrayReferenceType(String desc) {
		int dimensions = desc.lastIndexOf('[') + 1;
		String fullTypeString = desc.substring(dimensions);
		DataType fullType = getType(fullTypeString);
		String typeString = javaObjectName(fullType.toString());

		DataType arrayType = DataType.getType(typeString);
		arrayType.setDimension(dimensions);
		return arrayType;
	}

	private static DataType getReferenceType(String desc) {
		return DataType.getType(javaObjectName(javaObjectName(desc.substring(1))));
	}

	private static DataType getPrimitiveType(String desc) {
		switch (desc) {
			case "B":
				return DataType.BYTE;
			case "C":
				return DataType.CHAR;
			case "D":
				return DataType.DOUBLE;
			case "F":
				return DataType.FLOAT;
			case "I":
				return DataType.INT;
			case "J":
				return DataType.LONG;
			case "S":
				return DataType.SHORT;
			case "V":
				return DataType.VOID;
			case "Z":
				return DataType.BOOLEAN;
			default:
				throw new IllegalArgumentException("Unknown primitive type: " + desc);
		}
	}

//	public static String javaObjectName(String name) {
//		if (name.contains("$")) {
//			int lastName = name.lastIndexOf("$");
//			if (name.substring(lastName + 1).matches(".*[^0-9].*")) {
//				return name.substring(lastName + 1);
//			}
//			return name.substring(lastName);
//		}
//		return name;
//	}

	public static String javaObjectName(String objectName) {
		return Parser.getInstance().getInnerClassDisplayName(objectName).replaceAll("/", ".").replaceAll(";","");
	}

	public static String getFullClassName(String objectName) {
		return objectName.replace(';', '\0').replace('$','.');
	}

	public static boolean containsFlag(int value, int flag) {
		return (value & flag) != 0;
	}

	public static boolean isNotEmpty(String string) {
		return string != null && !string.isEmpty();
	}

	public static LocalVariableNode variableAtIndex(int index, List localVariables) {
		for (Object o: localVariables ) {
			LocalVariableNode variable = (LocalVariableNode) o;
			if (variable.index == index)
				return variable;
		}
		return null;
	}

	public static String getOpcodeString(int opCode) {
		String op = "";
		if (opCode > -1) {
			op = Printer.OPCODES[opCode];
		}
		return op;
	}

	public static String getCommaIfNeeded(int position) {
		if (position > 0) {
			return ", ";
		}
		return "";
	}

	public static boolean isConstructor(String name) {
		return name.equals("<init>");
	}
}
