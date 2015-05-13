package com.thesis.common;

import org.objectweb.asm.Type;

public class DataType {

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

		private SimpleType(DataType type) {
			mType = type;
		}

		public DataType getType(){
			return mType;
		}

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

	private String mTypeString;
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

	public static DataType getTypeFromDesc(String desc) {
		if (desc == null || desc.isEmpty()) return UNKNOWN;
		return new DataType(Type.getType(desc));
	}

	public static DataType getTypeFromObject(String objectType) {
		if (objectType == null || objectType.isEmpty()) return UNKNOWN;
		return new DataType(Type.getObjectType(objectType));
	}

	public static DataType getType(Type type) {
		return new DataType(type);
	}

	public boolean isReferenceType() {
		return !SimpleType.contains(this.mTypeString);
	}

	public boolean isArrayType() {
		return mDimension > 0;
	}

	public int getDimension() {
		return mDimension;
	}

	public void setDimension(int dimension) {
		mDimension = dimension;
	}

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
