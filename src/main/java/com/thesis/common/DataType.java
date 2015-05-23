package com.thesis.common;

import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.List;

/**
 * Representation of a Java type
 *
 * handles the translation from bytecode type to Java type
 */
public class DataType {

	/**
	 * Simple Java types
	 *
	 * INT, LONG, FLOAT, DOUBLE, BYTE, CHAR, SHORT, BOOLEAN, VOID
	 */
	private enum SimpleType {
		INT(new DataType("int")),
		LONG(new DataType("long")),
		FLOAT(new DataType("float")),
		DOUBLE(new DataType("double")),
		BYTE(new DataType("byte")),
		CHAR(new DataType("char")),
		SHORT(new DataType("short")),
		BOOLEAN(new DataType("boolean")),
		VOID(new DataType("void"));

		private DataType mType;

		SimpleType(DataType type) {
			mType = type;
		}

		/**
		 * @return data type instance of given enum instance
		 */
		public DataType getType(){
			return mType;
		}

		/**
		 * Method to check if the string is one of the simple types
		 * @param string type string
		 * @return true if the string is a simple type, false otherwise
		 */
		public static boolean contains(String string) {
			for(SimpleType type : SimpleType.values()) {
				if (type.toString().equals(string)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return mType.toString();
		}
	}

	public static final DataType INT = SimpleType.INT.getType();
	public static final DataType LONG = SimpleType.LONG.getType();
	public static final DataType FLOAT = SimpleType.FLOAT.getType();
	public static final DataType DOUBLE = SimpleType.DOUBLE.getType();
	public static final DataType BYTE = SimpleType.BYTE.getType();
	public static final DataType CHAR = SimpleType.CHAR.getType();
	public static final DataType SHORT = SimpleType.SHORT.getType();
	public static final DataType BOOLEAN = SimpleType.BOOLEAN.getType();
	public static final DataType VOID = SimpleType.VOID.getType();
	public static final DataType UNKNOWN = new DataType("java.lang.Object");

	/**
	 * List of JVM subtypes of int (boolean, byte, char, short, int)
	 */
	public static final List<DataType> INT_SUBTYPES = Arrays.asList(BOOLEAN, BYTE, CHAR, SHORT, INT);

	private String mTypeString;
	/**
	 * Array dimension, non-array types have dimension 0
	 */
	private int mDimension;

	private DataType(String typeString) {
		mTypeString = typeString;
		mDimension = 0;
	}

	private DataType(Type type) {
		mTypeString = type.getClassName();
		mDimension = 0;
		while (mTypeString.endsWith("[]")) {
			mTypeString = mTypeString.substring(0, mTypeString.length() - 2);
			mDimension++;
		}
	}

	/**
	 * Creates the type instance from type descriptor
	 * @param desc type descriptor
	 * @return data type instance
	 */
	public static DataType getTypeFromDesc(String desc) {
		if (desc == null || desc.isEmpty()) return UNKNOWN;
		return new DataType(Type.getType(desc));
	}

	/**
	 * Creates the type instance from Java type name
	 * @param objectType Java type name
	 * @return data type instance
	 */
	public static DataType getTypeFromObject(String objectType) {
		if (objectType == null || objectType.isEmpty()) return UNKNOWN;
		return new DataType(Type.getObjectType(objectType));
	}

	/**
	 * Creates the type instance from the ASM Type
	 * @param type ASM type instance
	 * @return data type instance
	 */
	public static DataType getType(Type type) {
		return new DataType(type);
	}

	/**
	 * @return returns true if the type is not a SimpleType {@link com.thesis.common.DataType.SimpleType}
	 */
	public boolean isReferenceType() {
		return !SimpleType.contains(this.mTypeString);
	}

	/**
	 * @return returns true if the type has a dimenstion at least one (1 dimensional array)
	 */
	public boolean isArrayType() {
		return mDimension > 0;
	}

	/**
	 * @return dimension of the type (1 is simple array)
	 */
	public int getDimension() {
		return mDimension;
	}

	public void setDimension(int dimension) {
		mDimension = dimension;
	}

	/**
	 * Prints the text representation of the type
	 * @return Java representation of the type
	 */
	public String print(){
		return Util.javaObjectName(mTypeString) + printBrackets();
	}

	private String printBrackets() {
		String brackets = "";
		if (isArrayType()) {
			for (int i = 0; i < mDimension; i++) {
				brackets += "[]";
			}
		}
		return brackets;
	}

	public String print(boolean isStatic) {
		if(isStatic) {
			return mTypeString.replace('$','.') + printBrackets();
		}
		return print();
	}

	/**
	 * Compares the types without generics
	 * @param other second data type
	 * @return true if the base types equal
	 */
	public boolean equalsWithoutGeneric(DataType other) {
		if (this == other) return true;
		if (other == null) return false;
		
		String thisWithoutGeneric = getTypeWithoutGenerics(mTypeString);
		String otherWithoutGeneric = getTypeWithoutGenerics(other.mTypeString);

		return thisWithoutGeneric.equals(otherWithoutGeneric);
	}

	private String getTypeWithoutGenerics(String typeString) {
		int diamondLocation = typeString.indexOf('<');

		if (diamondLocation > -1) {
			return typeString.substring(0, diamondLocation);
		}
		return typeString;
	}

	@Override
	public String toString() {
		return mTypeString;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DataType type = (DataType) o;

		if (!mTypeString.equals(type.mTypeString)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return mTypeString.hashCode();
	}
}
