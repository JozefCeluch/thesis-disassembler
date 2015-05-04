package com.thesis.expression.variable;

import com.thesis.common.DataType;
import com.thesis.common.SignatureVisitor;
import com.thesis.translator.ExpressionStack;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.LocalVariableNode;

public class LocalVariable extends Variable {

	private int mIndex;
	private boolean mIsArgument;
	private Label mStart;
	private Label mEnd;
	private boolean isAdded = false;

	public LocalVariable(int index) {
		super();
		this.mIndex = index;
	}

	public LocalVariable(String name, int index) {
		this(index);
		mName = name;
	}

	public LocalVariable(LocalVariableNode variableNode) {
		super(variableNode.name, DataType.getTypeFromDesc(variableNode.desc));

		if (variableNode.signature != null && !variableNode.signature.isEmpty()) {
			SignatureVisitor visitor = new SignatureVisitor(Opcodes.ACC_PRIVATE);
			SignatureReader reader = new SignatureReader(variableNode.signature);
			reader.acceptType(visitor);
			mType = DataType.getTypeFromObject(visitor.getDeclaration());
		}
		mIndex = variableNode.index;
		mStart = variableNode.start.getLabel();
		mEnd = variableNode.end.getLabel();
		mDebugType = true;
	}

	public LocalVariable(String name, DataType type, int index) {
		super(name, type);
		mIndex = index;
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

	public int getStartLabel(ExpressionStack stack) {
		return stack.getLabelId(mStart);
	}

	public int getEndLabelId(ExpressionStack stack) {
		return stack.getLabelId(mEnd);
	}

	public boolean isAdded() {
		return isAdded;
	}

	public void setAdded(boolean added) {
		this.isAdded = added;
	}

	@Override
	public String toString() {
		return mName;
	}
}
