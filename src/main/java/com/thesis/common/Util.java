package com.thesis.common;

import com.thesis.file.Disassembler;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.util.Printer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * Map that maps the full inner class names to their displayed names
	 *
	 * full inner class name, displayed inner class name
	 */
	private static Map<String, String> mInnerClassMap;

	/**
	 * @param objectName full object name in bytecode representation
	 * @return Java class name representation without outer classes
	 */
	public static String javaObjectName(String objectName) {
		return getInnerClassDisplayName(objectName);
	}

	/**
	 * Adds the trimmed name of inner class to the map
	 * @param fullName full name of inner class
	 * @param displayName name of the inner class without the enclosing classes
	 */
	public static void addInnerClassName(String fullName, String displayName) {
		if (mInnerClassMap == null) {
			mInnerClassMap = new HashMap<>();
		}
		if (!mInnerClassMap.containsKey(fullName)) {
			mInnerClassMap.put(fullName, displayName);
		}
	}

	/**
	 * Returns trimmed name of the inner class with the given full name
	 * @param fullName fill name of the inner class
	 * @return trimmed name, if there is any stored, otherwise the provided full name
	 */
	public static String getInnerClassDisplayName(String fullName) {
		if (mInnerClassMap == null) {
			return fullName;
		}
		String result = mInnerClassMap.get(fullName);

		return result != null ? result : fullName;
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
	 *
	 * @param num tested number
	 * @param min inclusive minimum value
	 * @param max inclusive maximum value
	 * @return true if num is between min and max
	 */
	public static boolean isBetween(int num, int min, int max) {
		return num >= min && num <= max;
	}
}
