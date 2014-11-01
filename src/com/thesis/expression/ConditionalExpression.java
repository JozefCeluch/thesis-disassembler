package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.util.Printer;

public abstract class ConditionalExpression extends Expression {

	private int mDestination;

	public ConditionalExpression(AbstractInsnNode instruction, int jumpDestination) {
		super(instruction);
		mDestination = jumpDestination;
	}

	public ConditionalExpression(int jumpDestination) {
		super(null);
		mDestination = jumpDestination;
	}

	public int getDestination() {
		return mDestination;
	}

	@Override
	public String getType() {
		return "boolean";
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
