package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.util.Printer;

import java.io.IOException;
import java.io.Writer;

public class LogicalExpression extends ConditionalExpression {

	private Expression mLeftSide;
	private Expression mRightSide;

	public LogicalExpression(AbstractInsnNode instruction, Expression leftSide, Expression rightSide) {
		super(instruction);
		mLeftSide = leftSide;
		mRightSide = rightSide;
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeftSide.write(writer);
		writer.append(" ").append(makeOperand().neg().toString()).append(" ");
		mRightSide.write(writer);
	}

	private Operand makeOperand() {
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
