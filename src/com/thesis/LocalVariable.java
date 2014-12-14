package com.thesis;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import org.objectweb.asm.tree.LocalVariableNode;

public class LocalVariable extends Variable {

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
		super(variableNode.name, Util.getType(variableNode.desc)); //todo use signature for more complex types
		mIndex = variableNode.index;
		mDebugType = true;
	}

	public LocalVariable(String name, DataType type, int index) {
		super(name, type);
		mIndex = index;
		mDebugType = false;
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

	public boolean hasDebugType(){
		return mDebugType;
	}

	@Override
	public String toString() {
		return mName;
	}
}
