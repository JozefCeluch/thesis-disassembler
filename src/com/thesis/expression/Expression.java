package com.thesis.expression;

import com.thesis.Writable;
import com.thesis.common.DataType;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.StringWriter;

public abstract class Expression implements Writable {

	protected AbstractInsnNode mInstruction;
	protected DataType mType;
	protected DataType mCastType;

	public Expression(AbstractInsnNode instruction) {
		mInstruction = instruction;
	}

	abstract public DataType getType();

	public void setType(DataType type){
		mType = type;
	}

	public void setCastType(DataType type) {
		mCastType = type;
	}

	public boolean hasType() {
		return mType != null && !mType.equals(DataType.UNKNOWN);
	}

	public boolean isVirtual() {
		return false;
	}

	abstract public void prepareForStack(ExpressionStack stack);

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		try {
			write(writer);
		} catch (IOException e) {
			//todo
			e.printStackTrace();
		}
		return  writer.toString();
	}
}
