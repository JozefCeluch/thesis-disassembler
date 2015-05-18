package com.thesis.expression.variable;

import com.thesis.common.DataType;

public class GlobalVariable extends Variable {

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