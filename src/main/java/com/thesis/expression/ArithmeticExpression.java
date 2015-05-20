package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;
import org.objectweb.asm.util.Printer;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * Expression that represents a binary operation
 *<p>
 * used for the following instructions:
 * IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB,
 * IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM,
 * FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR,
 * IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR
 *
 * before being pushed to the stack, it pops right and left sides of the expression from the top of the stack
 *
 */
public class ArithmeticExpression extends Expression {

	private Expression mLeftSide;
	private Expression mRightSide;

	public ArithmeticExpression(int opCode) {
		super(opCode);
	}

	public void setLeftSide(Expression leftSide) {
		mLeftSide = leftSide;
	}

	public void setRightSide(Expression rightSide) {
		mRightSide = rightSide;
	}

	private Operand makeOperand(){ //TODO just compare the opcodes not strings
		String opcode = Printer.OPCODES[mOpCode];
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
		if (mCastType != null) {
			writer.append("(").append(mCastType.toString()).append(") (");
		}
		writeSubExpression(mLeftSide, writer);
		writer.append(" ").append(op.toString()).append(" ");
		writeSubExpression(mRightSide, writer);

		if (mCastType != null) {
			writer.write(")");
		}
	}

	private void writeSubExpression(Expression exp, Writer writer) throws IOException {
		if (!(exp instanceof PrimaryExpression)) {
			writer.append('(');
			exp.write(writer);
			writer.append(')');
		} else {
			exp.write(writer);
		}
	}

	@Override
	public DataType getType() {
		if (mType == null) {
			mergeTypes();
		}
		return mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		mRightSide = stack.pop();
		mLeftSide = stack.pop();
		mergeTypes();
	}

	private void mergeTypes() {
		DataType leftType = mLeftSide.getType();
		DataType rightType = mRightSide.getType();

		if (leftType == null && rightType == null) {
			return;
		}
		if (leftType == null) {
			mType = rightType;
		} else if (rightType == null) {
			mType = leftType;
		} else if (leftType.equals(DataType.UNKNOWN) && !rightType.equals(DataType.UNKNOWN)) {
			mType = rightType;
		} else if (!leftType.equals(DataType.UNKNOWN) && rightType.equals(DataType.UNKNOWN)) {
			mType = leftType;
		} else if (DataType.INT_SUBTYPES.contains(leftType) && !DataType.INT_SUBTYPES.contains(rightType)) {
			mType = rightType;
		}
		mType = leftType;
	}
}
