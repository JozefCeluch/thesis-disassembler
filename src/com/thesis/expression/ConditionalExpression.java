package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.util.Printer;

public abstract class ConditionalExpression extends Expression {

	protected int mConditionalJumpDest = -1;
	protected int mGoToDest = -1;
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
		String opcode = Printer.OPCODES[mInstruction.getOpcode()];
		if (opcode.endsWith("EQ")) {
			return Operand.EQUAL;
		}
		if (opcode.endsWith("NE")){
			return Operand.NOT_EQUAL;
		}
		if (opcode.endsWith("GE")){
			return Operand.GREATER_EQUAL;
		}
		if (opcode.endsWith("GT")){
			return Operand.GREATER_THAN;
		}
		if (opcode.endsWith("LE")){
			return Operand.LESS_EQUAL;
		}
		if (opcode.endsWith("LT")){
			return Operand.LESS_THAN;
		}

		return Operand.ERR;
	}
}
