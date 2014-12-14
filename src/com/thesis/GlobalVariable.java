package com.thesis;

import com.thesis.common.DataType;

public class GlobalVariable extends Variable {

	private DataType mOwner;

	public GlobalVariable(String name, DataType owner) {
		super(name, owner);
	}

	public DataType getOwner() {
		return mOwner;
	}

	public void setOwner(DataType owner) {
		mOwner = owner;
	}

	@Override
	public String toString() {
		return mOwner!= null ? mOwner.toString() + "." + mName : mName;
	}
}
