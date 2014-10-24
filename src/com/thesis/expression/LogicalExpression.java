package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.io.Writer;

public class LogicalExpression extends Expression {

	private Expression mLeftSide;
	private Expression mRightSide;

	public LogicalExpression(AbstractInsnNode instruction, Expression leftSide, Expression rightSide) {
		super(instruction);
		mLeftSide = leftSide;
		mRightSide = rightSide;
		mType = "boolean";
	}

	@Override
	public String getType() {
		return mType;
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeftSide.write(writer);
		writer.append(" ").append(String.valueOf(mInstruction.getOpcode())).append(" ");
		mRightSide.write(writer);
	}

	/**
	 *
	 if_acmpeq
	 if_acmpne
	 if_icmpeq
	 if_icmpge
	 if_icmpgt
	 if_icmple
	 if_icmplt
	 if_icmpne

	 ifeq
	 ifge
	 ifgt
	 ifle
	 iflt
	 ifne

	 ifnonnull
	 ifnull
	 */
	private String makeOperand() {
		return null;
	}
}
