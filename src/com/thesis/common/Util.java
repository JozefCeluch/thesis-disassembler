package com.thesis.common;

import com.thesis.file.Parser;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.util.Printer;

import java.util.List;

public class Util {

	public static final String ARGUMENT_NAME_BASE = "arg";

	public static String javaObjectName(String objectName) {
		return Parser.getInstance().getInnerClassDisplayName(objectName);
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

	/**
	 * min and max are inclusive
	 * @return true if num is between min and max
	 */
	public static boolean isBetween(int num, int min, int max) {
		return num >= min && num <= max;
	}
}
