package com.thesis;

import com.thesis.common.Util;
import org.objectweb.asm.tree.LocalVariableNode;

public class LocalVariable {

	private String mName;

	private String mType;

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

	public LocalVariable(String name, String type, int index, boolean isArgument) {
		mName = name;
		mType = type;
		mIndex = index;
		mIsArgument = isArgument;
		mDebugType = false;
	}

	public String getName() {
		return mName;
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

	public boolean hasType() {
		return mType != null && !mType.isEmpty();
	}

	public boolean hasDebugType(){
		return mDebugType;
	}
}
