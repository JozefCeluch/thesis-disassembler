package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.util.Printer;

import java.io.IOException;
import java.io.Writer;

public class ArithmeticExpression extends Expression {

	private Expression mLeftSide;
	private Expression mRightSide;

	public ArithmeticExpression(AbstractInsnNode instruction, Expression leftSide, Expression rightSide) {
		super(instruction);
		mLeftSide = leftSide;
		mRightSide = rightSide;
	}

	private String makeOperand(){
		String opcode = Printer.OPCODES[mInstruction.getOpcode()];
		if (opcode.endsWith("MUL")){
			return Operand.MULTIPLY.toString();
		}
		if (opcode.endsWith("DIV")){
			return Operand.DIVIDE.toString();
		}
		if (opcode.endsWith("ADD")){
			return Operand.ADD.toString();
		}
		if (opcode.endsWith("SUB")){
			return Operand.SUBTRACT.toString();
		}
		if (opcode.endsWith("REM")){
			return Operand.REMAINDER.toString();
		}
		if (opcode.endsWith("XOR")){
			return Operand.BITWISE_XOR.toString();
		}
		if (opcode.endsWith("OR")){
			return Operand.BITWISE_OR.toString();
		}
		if (opcode.endsWith("AND")){
			return Operand.BITWISE_AND.toString();
		}
		if (opcode.endsWith("USHR")){
			return Operand.LOGICAL_SHIFT_RIGHT.toString();
		}
		if (opcode.endsWith("SHR")){
			return Operand.ARITHMETIC_SHIFT_RIGHT.toString();
		}
		if (opcode.endsWith("SHL")){
			return Operand.ARITHMETIC_SHIFT_LEFT.toString();
		}
		return "UNKNOWN";
	}

	@Override
	public void write(Writer writer) throws IOException {
		mLeftSide.write(writer);
		writer.append(" ").append(makeOperand()).append(" ");
		mRightSide.write(writer);
	}

	@Override
	public String getType() {
		return mLeftSide.getType();
	}
}
