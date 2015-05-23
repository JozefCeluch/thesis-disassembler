package com.thesis.expression.variable;

import com.thesis.common.DataType;

/**
 * A variable class representing a field in bytecode
 */
public class GlobalVariable extends Variable {

	/**
	 * Enclosing class
	 */
	private DataType mOwner;

	public GlobalVariable(String name, DataType type, DataType owner) {
		super(name, type);
		mOwner = owner;
	}

	public DataType getOwner() {
		return mOwner;
	}

	public void setOwner(DataType owner) {
		mOwner = owner;
	}

	@Override
	public String toString() {
		return mOwner!= null ? mOwner.print() + "." + mName : mName;
	}

	@Override
	public String write() {
		return toString();
	}
}
