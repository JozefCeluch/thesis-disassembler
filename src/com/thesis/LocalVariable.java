package com.thesis;

import com.thesis.common.Util;
import org.objectweb.asm.tree.LocalVariableNode;

public class LocalVariable {

	private String mName;

	private String mType;

	private int mIndex;

	private boolean mIsArgument;

	public LocalVariable(int index) {
		this.mIndex = index;
	}

	public LocalVariable(String name, int index) {
		this(index);
		mName = name;
	}

	public LocalVariable(LocalVariableNode variableNode) {
		mName = variableNode.name;
		mIndex = variableNode.index;
		mType = Util.getType(variableNode.desc); //todo use signature for more complex types
	}

	public LocalVariable(String name, String type, int index, boolean isArgument) {
		mName = name;
		mType = type;
		mIndex = index;
		mIsArgument = isArgument;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		this.mType = type;
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		this.mIndex = index;
	}

	public boolean isArgument() {
		return mIsArgument;
	}

}
