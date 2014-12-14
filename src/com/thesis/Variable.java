package com.thesis;

import com.thesis.common.DataType;

public class Variable {

	protected String mName;
	protected DataType mType;

	public Variable() {
	}

	public Variable(String name, DataType type) {
		mName = name;
		mType = type;
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

	public boolean hasType() {
		return mType != null;
	}

	@Override
	public String toString() {
		return mName;
	}
}
