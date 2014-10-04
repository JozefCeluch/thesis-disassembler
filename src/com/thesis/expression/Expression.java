package com.thesis.expression;

import com.thesis.Writable;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.StringWriter;

public abstract class Expression implements Writable {

	protected AbstractInsnNode mInstruction;

	protected Expression() {
	}

	protected Expression(AbstractInsnNode instruction) {
		mInstruction = instruction;
	}

	public AbstractInsnNode getInstruction() {
		return mInstruction;
	}

	public void setInstruction(AbstractInsnNode instruction) {
		mInstruction = instruction;
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
