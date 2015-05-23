package com.thesis.expression.variable;

import com.thesis.common.DataType;

/**
 * General class representing a variable
 */
public abstract class Variable {
	/**
	 * Variable name
	 */
	protected String mName;

	/**
	 * Variable typs
	 */
	protected DataType mType;

	/**
	 * Flag to check if variable has a type from debug information
	 */
	protected boolean mDebugType;

	/**
	 * Flag to check if variable should print its type
	 */
	protected boolean mPrintType;

	public Variable() {
		mDebugType = false;
		mType = DataType.UNKNOWN;
	}

	public Variable(String name, DataType type) {
		mDebugType = false;
		mName = name;
		mType = type != null ? type : DataType.UNKNOWN;
	}

	public DataType getType() {
		return mType;
	}

	public void setType(DataType type) {
		this.mType = type;
	}

	public void setName(String name) {
		mName = name;
	}

	/**
	 * @return if variable has a defined type
	 */
	public boolean hasType() {
		return mType != null && !mType.toString().isEmpty() && !mType.equals(DataType.UNKNOWN);
	}

	public boolean hasDebugType(){
		return mDebugType;
	}

	public void setPrintType(boolean printType) {
		mPrintType = printType;
	}

	public abstract String write();
}
