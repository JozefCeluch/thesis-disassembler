package com.thesis.expression;

import com.thesis.common.Writable;
import com.thesis.common.DataType;
import com.thesis.exception.DecompilerRuntimeException;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Abstract general representation of a certain part of Java code that can be inferred from a bytecode instruction
 */
public abstract class Expression implements Writable {

	/**
	 * opcode of the instruction that caused the expression
	 */
	protected int mOpCode;

	/**
	 * Expression type
	 */
	protected DataType mType;

	/**
	 * Expression cast type
	 */
	protected DataType mCastType;

	/**
	 * Line where the expression ocurred in the original code
	 */
	protected int mLine;

	/**
	 * Flag noting if the expression is virtual (i.e. should not be printed)
	 */
	protected boolean mIsVirtual;

	public Expression(int opCode) {
		mOpCode = opCode;
		mIsVirtual = false;
	}

	public DataType getType() {
		return mType;
	}

	public void setType(DataType type){
		mType = type;
	}

	public void setCastType(DataType type) {
		mCastType = type;
	}

	public int getLine() {
		return mLine;
	}

	public void setLine(int line) {
		mLine = line;
	}

	public void setOpCode(int opCode) {
		mOpCode = opCode;
	}

	/**
	 * @return if expression has a defined type
	 */
	public boolean hasType() {
		return mType != null && !mType.toString().isEmpty() && !mType.equals(DataType.UNKNOWN);
	}

	public boolean isVirtual() {
		return mIsVirtual;
	}

	/**
	 * Method called before expression is pushed onto the stack
	 *
	 * Expressions can modify the top of the stack here, pop any expressions they require from the stack
	 * @param stack where the expression is being pushed on
	 */
	abstract public void prepareForStack(ExpressionStack stack);

	/**
	 * Method called after the method is pushed onto the stack
	 *
	 * Expressions can modify the stack in case they need to adjust something after being pushed
	 * @param stack where the expression was pushed
	 */
	public void afterPush(ExpressionStack stack){

	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		try {
			write(writer);
		} catch (IOException e) {
			throw new DecompilerRuntimeException("Error while writing the expression: " + e.getMessage());
		}
		return  writer.toString();
	}
}
