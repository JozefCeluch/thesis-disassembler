package com.thesis.expression;

import com.thesis.Writable;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.StringWriter;

public abstract class Expression implements Writable {

	protected AbstractInsnNode mInstruction;
	protected String mType;

	public Expression(AbstractInsnNode instruction) {
		mInstruction = instruction;
	}

	abstract public String getType();

	public void setType(String type){
		mType = type;
	}

	public boolean hasType() {
		return mType != null && !mType.isEmpty();
	}

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
