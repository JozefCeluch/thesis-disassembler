package com.thesis.expression;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.util.Printer;

import java.io.IOException;
import java.io.Writer;

public class ArithmeticExpression extends Expression {

	private Expression mLeftSide;
	private Expression mRightSide;

	public ArithmeticExpression(AbstractInsnNode instruction) {
		super(instruction);
	}

	public void setLeftSide(Expression leftSide) {
		mLeftSide = leftSide;
	}

	public void setRightSide(Expression rightSide) {
		mRightSide = rightSide;
	}

	private Operand makeOperand(){
		String opcode = Printer.OPCODES[mInstruction.getOpcode()];
		if (opcode.endsWith("MUL")){
			return Operand.MULTIPLY;
		}
		if (opcode.endsWith("DIV")){
			return Operand.DIVIDE;
		}
		if (opcode.endsWith("ADD")){
			return Operand.ADD;
		}
		if (opcode.endsWith("SUB")){
			return Operand.SUBTRACT;
		}
		if (opcode.endsWith("REM")){
			return Operand.REMAINDER;
		}
		if (opcode.endsWith("XOR")){
			return Operand.BITWISE_XOR;
		}
		if (opcode.endsWith("OR")){
			return Operand.BITWISE_OR;
		}
		if (opcode.endsWith("AND")){
			return Operand.BITWISE_AND;
		}
		if (opcode.endsWith("USHR")){
			return Operand.LOGICAL_SHIFT_RIGHT;
		}
		if (opcode.endsWith("SHR")){
			return Operand.ARITHMETIC_SHIFT_RIGHT;
		}
		if (opcode.endsWith("SHL")){
			return Operand.ARITHMETIC_SHIFT_LEFT;
		}
		return Operand.ERR;
	}

	@Override
	public void write(Writer writer) throws IOException {
		Operand op = makeOperand();
		writeSubExpression(mLeftSide, writer, op);

		writer.append(" ").append(op.toString()).append(" ");

		writeSubExpression(mRightSide, writer, op);
	}

	private void writeSubExpression(Expression exp, Writer writer, Operand op) throws IOException {
		if ((op == Operand.MULTIPLY || op == Operand.DIVIDE) && !(exp instanceof PrimaryExpression)) {
			writer.append('(');
			exp.write(writer);
			writer.append(')');
		} else {
			exp.write(writer);
		}
	}

	@Override
	public String getType() {
		return mLeftSide.getType(); //todo think why?
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		mRightSide = stack.pop();
		mLeftSide = stack.pop();
	}
}
