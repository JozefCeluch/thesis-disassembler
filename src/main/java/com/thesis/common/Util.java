package com.thesis.common;

import com.thesis.file.Disassembler;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.util.Printer;

import java.util.List;

/**
 * Arbitrary helper methods
 */
public class Util {

	/**
	 * Generic base of argument name, used in case there is no real name
	 */
	public static final String ARGUMENT_NAME_BASE = "arg";

	/**
	 * Generic base of variable name
	 */
	public static final String VARIABLE_NAME_BASE = "var";

	/**
	 * @param objectName full object name in bytecode representation
	 * @return Java class name representation without outer classes
	 */
	public static String javaObjectName(String objectName) {
		return Disassembler.getInstance().getInnerClassDisplayName(objectName);
	}

	/**
	 * @param value mask
	 * @param flag required flag
	 * @return true if the value contains the flag
	 */
	public static boolean containsFlag(int value, int flag) {
		return (value & flag) != 0;
	}

	/**
	 *
	 * @param index variable location index
	 * @param localVariables list of local variables
	 * @return local variable at given index
	 */
	public static LocalVariableNode variableAtIndex(int index, List localVariables) {
		for (Object o: localVariables ) {
			LocalVariableNode variable = (LocalVariableNode) o;
			if (variable.index == index)
				return variable;
		}
		return null;
	}

	/**
	 * Converts the opcode to string
	 * @param opCode instruction number
	 * @return mnemonic representation of the opcode
	 */
	public static String getOpcodeString(int opCode) {
		String op = "";
		if (opCode > -1) {
			op = Printer.OPCODES[opCode];
		}
		return op;
	}

	/**
	 * Returns comma if position id bigger than 0
	 * @param position pos
	 * @return comma or empty string if possition is 0
	 */
	public static String getCommaIfNeeded(int position) {
		if (position > 0) {
			return ", ";
		}
		return "";
	}

	/**
	 * @param name method name
	 * @return true if method is constuctor
	 */
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
