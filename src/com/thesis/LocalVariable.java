package com.thesis;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import org.objectweb.asm.tree.LocalVariableNode;

public class LocalVariable {

	private String mName;

	private DataType mType;

	private int mIndex;

	private boolean mIsArgument;

	private boolean mDebugType;

	public LocalVariable(int index) {
		this.mIndex = index;
		mDebugType = false;
	}

	public LocalVariable(String name, int index) {
		this(index);
		mName = name;
	}

	public LocalVariable(LocalVariableNode variableNode) {
		mName = variableNode.name;
		mIndex = variableNode.index;
		mType = Util.getType(variableNode.desc); //todo use signature for more complex types
		mDebugType = true;
	}

	public LocalVariable(String name, DataType type, int index) {
		mName = name;
		mType = type;
		mIndex = index;
		mDebugType = false;
	}

	public String getName() {
		return mName;
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

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		mIndex = index;
	}

	public void setIsArgument(boolean isArgument) {
		mIsArgument = isArgument;
	}

	public boolean isArgument() {
		return mIsArgument;
	}

	public boolean hasType() {
		return mType != null;
	}

	public boolean hasDebugType(){
		return mDebugType;
	}
}
