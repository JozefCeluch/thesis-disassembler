package com.thesis.common;

import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.util.Printer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

public class Util {

	public static final String ARGUMENT_NAME_BASE = "arg";

	public static String getType(String desc){
		String type;
		if (desc.startsWith("L")) {
			type = getReferenceType(desc);
		}else if (desc.startsWith("[")) {
			type = getArrayReferenceType(desc);
		} else {
			type = getPrimitiveType(desc);
		}
		return removeOuterClasses(type);
	}

	public static String removeOuterClasses(String name) {
		if (name.contains("$")) {
			int lastName = name.lastIndexOf("$");
			return name.substring(lastName + 1);
		}
		return name;
	}

	public static String getArrayReferenceType(String desc) {
		int dimensions = desc.lastIndexOf('[') + 1;
		String type = desc.substring(dimensions);
		String result = getType(type);
		for (int i = 0; i < dimensions; i++) {
			result += "[]";
		}
		return result;
	}

	public static String getReferenceType(String desc) {
		return javaObjectName(desc.substring(1));
	}

	public static String getPrimitiveType(String desc) {
		String type;
		switch (desc) {
			case "B":
				type = "byte";
				break;
			case "C":
				type = "char";
				break;
			case "D":
				type = "double";
				break;
			case "F":
				type = "float";
				break;
			case "I":
				type = "int";
				break;
			case "J":
				type = "long";
				break;
			case "S":
				type = "short";
				break;
			case "V":
				type = "void";
				break;
			case "Z":
				type = "boolean";
				break;
			default:
				System.out.println("type: " + desc);
				throw new IllegalArgumentException("Unknown primitive type");
		}
		return type;
	}

	public static String javaObjectName(String objectName) {
		return objectName.replaceAll("/", ".").replaceAll(";","");
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
}
