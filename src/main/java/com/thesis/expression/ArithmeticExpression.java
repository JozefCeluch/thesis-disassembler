package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import com.thesis.translator.ExpressionStack;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.Writer;

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

	private Operator makeOperator() {
		if (Util.isBetween(mOpCode, Opcodes.IMUL, Opcodes.DMUL)) {
			return Operator.MULTIPLY;
		}
		if (Util.isBetween(mOpCode, Opcodes.IDIV, Opcodes.DDIV)) {
			return Operator.DIVIDE;
		}
		if (Util.isBetween(mOpCode, Opcodes.IADD, Opcodes.DADD)) {
			return Operator.ADD;
		}
		if (Util.isBetween(mOpCode, Opcodes.ISUB, Opcodes.DSUB)) {
			return Operator.SUBTRACT;
		}
		if (Util.isBetween(mOpCode, Opcodes.IREM, Opcodes.DREM)) {
			return Operator.REMAINDER;
		}
		if (Util.isBetween(mOpCode, Opcodes.IXOR, Opcodes.LXOR)) {
			return Operator.BITWISE_XOR;
		}
		if (Util.isBetween(mOpCode, Opcodes.IOR, Opcodes.LOR)) {
			return Operator.BITWISE_OR;
		}
		if (Util.isBetween(mOpCode, Opcodes.IAND, Opcodes.LAND)) {
			return Operator.BITWISE_AND;
		}
		if (Util.isBetween(mOpCode, Opcodes.IUSHR, Opcodes.LUSHR)) {
			return Operator.LOGICAL_SHIFT_RIGHT;
		}
		if (Util.isBetween(mOpCode, Opcodes.ISHR, Opcodes.LSHR)) {
			return Operator.ARITHMETIC_SHIFT_RIGHT;
		}
		if (Util.isBetween(mOpCode, Opcodes.ISHL, Opcodes.LSHL)) {
			return Operator.ARITHMETIC_SHIFT_LEFT;
		}
		if (Util.isBetween(mOpCode, Opcodes.INEG, Opcodes.DNEG)) {
			return Operator.SUBTRACT;
		}
		return Operator.ERR;
	}

	@Override
	public void write(Writer writer) throws IOException {
		Operator op = makeOperator();
		if (mCastType != null) {
			writer.append("(").append(mCastType.toString()).append(") (");
		}
		if (mLeftSide != null) {
			writeSubExpression(mLeftSide, writer);
			writer.append(" ");
		}
		writer.append(op.toString()).append(" ");
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
		if (!Util.isBetween(mOpCode, Opcodes.INEG, Opcodes.DNEG)) {
			mLeftSide = stack.pop();
		}
		mergeTypes();
	}

	private void mergeTypes() {
		DataType leftType = null;
		DataType rightType = null;
		if (mLeftSide != null) {
			leftType = mLeftSide.getType();
		}
		if (mRightSide != null) {
			rightType = mRightSide.getType();
		}

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
		} else {
			mType = leftType;
		}
	}
}
