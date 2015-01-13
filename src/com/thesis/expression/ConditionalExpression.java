package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.util.Printer;

import java.util.ArrayList;

public abstract class ConditionalExpression extends Expression {

	public static final int NO_DESTINATION = -1;

	protected int mConditionalJumpDest = NO_DESTINATION;
	protected int mGoToDest = NO_DESTINATION;
	protected ExpressionStack thenBranch;
	protected ExpressionStack elseBranch;

	public ConditionalExpression(AbstractInsnNode instruction, int jumpDestination) {
		super(instruction);
		mType = DataType.BOOLEAN;
		mConditionalJumpDest = jumpDestination;
		thenBranch = new ExpressionStack();
		elseBranch = new ExpressionStack();
	}

	public ConditionalExpression(int jumpDestination) {
		this(null, jumpDestination);
	}

	public int getConditionalJumpDest() {
		return mConditionalJumpDest;
	}

	public void setConditionalJumpDest(int conditionalJumpDest) {
		mConditionalJumpDest = conditionalJumpDest;
	}

	public int getGoToDest() {
		return mGoToDest;
	}

	public void setGoToDest(int goToDest) {
		mGoToDest = goToDest;
	}

	public ExpressionStack getThenBranch() {
		return thenBranch;
	}

	public ExpressionStack getElseBranch() {
		return elseBranch;
	}

	@Override
	public DataType getType() {
		return mType;
	}

	protected Operand makeOperand() {
		int opcode = mInstruction.getOpcode();
		if (opcode == Opcodes.IFEQ || opcode == Opcodes.IF_ACMPEQ || opcode == Opcodes.IF_ICMPEQ || opcode == Opcodes.IFNULL) {
			return Operand.EQUAL;
		}
		if (opcode == Opcodes.IFNE || opcode == Opcodes.IF_ACMPNE || opcode == Opcodes.IF_ICMPNE || opcode == Opcodes.IFNONNULL){
			return Operand.NOT_EQUAL;
		}
		if (opcode == Opcodes.IFGE || opcode == Opcodes.IF_ICMPGE){
			return Operand.GREATER_EQUAL;
		}
		if (opcode == Opcodes.IFGT || opcode == Opcodes.IF_ICMPGT){
			return Operand.GREATER_THAN;
		}
		if (opcode == Opcodes.IFLE || opcode == Opcodes.IF_ICMPLE){
			return Operand.LESS_EQUAL;
		}
		if (opcode == Opcodes.IFLT || opcode == Opcodes.IF_ICMPLT){
			return Operand.LESS_THAN;
		}

		return Operand.ERR;
	}
}
