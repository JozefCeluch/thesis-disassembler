package com.thesis.expression;

import com.thesis.expression.variable.Variable;
import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents assignment of an expression to a variable
 *<p>
 * variable is always {@link com.thesis.expression.AssignmentExpression.LeftHandSide}
 * used for the following instructions:
 * PUTFIELD, PUTSTATIC, IINC, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE
 */
public class AssignmentExpression extends  Expression{

	private LeftHandSide mLeftSide;
	private Expression mRightSide;
	private boolean mPrintType;

	public AssignmentExpression(int opCode, LeftHandSide leftSide) {
		super(opCode);
		mLeftSide = leftSide;
		mType = mLeftSide.getType();
		mPrintType = false;
	}

	public AssignmentExpression(int opCode, LeftHandSide leftSide, Expression rightSide) {
		this(opCode, leftSide);
		rightSide.setType(leftSide.getType());
		mRightSide = rightSide;
	}

	public void setRightSide(Expression rightSide) {
		mRightSide = rightSide;
	}

	public void setPrintType(boolean printType) {
		mPrintType = printType;
	}

	public Expression getRightSide() {
		return mRightSide;
	}

	public Variable getVariable() {
		return mLeftSide.getVariable();
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (mPrintType) {
			writer.append(mType.print()).append(" ");
		}
		mLeftSide.write(writer);
		writer.append(makeCorrectOperator());
		mRightSide.write(writer);
	}

	@Override
	public DataType getType() {
		return mLeftSide.getType();
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		if (mRightSide != null) return; //the expression already has right side
		if (!stack.isEmpty() ) {
			mRightSide = stack.pop();

			if (mRightSide.hasType() && !mLeftSide.hasType()) {
				mLeftSide.setType(mRightSide.getType());
			} else if (mLeftSide.hasType() && mRightSide instanceof PrimaryExpression && ((PrimaryExpression) mRightSide).isConstant()) {
				mRightSide.setType(mLeftSide.getType());
			}

			if (mRightSide.mCastType != null) {
				mLeftSide.setType(mRightSide.mCastType);
			} else if (mLeftSide.hasType() && !mLeftSide.getType().equals(mRightSide.getType())) {
				mRightSide.mCastType = mLeftSide.getType();
			}
		}
	}

	private String makeCorrectOperator() {
		String op;
		if (mOpCode == Opcodes.IINC) {
			op = " += ";
		} else {
			op = " = ";
		}

		return op;
	}

	/**
	 * Expression used in the {@link AssignmentExpression} that represents a variable
	 */
	public static class LeftHandSide extends Expression {

		private Variable mVariable;

		public LeftHandSide(int opCode, Variable variable) {
			super(opCode);
			mVariable = variable;
		}

		@Override
		public DataType getType() {
			return mVariable.getType();
		}

		@Override
		public void setType(DataType type) {
			super.setType(type);
			mVariable.setType(type);
		}

		@Override
		public boolean hasType() {
			return mVariable.hasType();
		}

		@Override
		public void prepareForStack(ExpressionStack stack) {
			//no preparation necessary
		}

		@Override
		public void write(Writer writer) throws IOException {
			writer.write(mVariable.toString());
		}

		public Variable getVariable() {
			return mVariable;
		}
	}
}
