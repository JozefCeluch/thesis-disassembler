package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.expression.stack.ExpressionStack;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.Writer;

public class ReturnExpression extends Expression {

	private Expression mExpression;

	public ReturnExpression(int opCode) {
		super(opCode);
		mType = getReturnType(opCode);
	}

	public void setExpression(Expression expression) {
		mExpression = expression;
	}

	@Override
	public DataType getType() {
		return mType.isReferenceType() ? mExpression.getType() : mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		if (!mType.equals(DataType.VOID)) {
			mExpression = stack.pop();
		}
	}

	@Override
	public boolean isVirtual() {
		return DataType.VOID.equals(mType);
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (DataType.VOID.equals(mType)) return;
		writer.write("return");
		if (mExpression != null) {
			writer.write(' ');
			mExpression.write(writer);
		}
	}

	private static DataType getReturnType(int opcode) {
		switch (opcode){
			case Opcodes.IRETURN:
				return DataType.INT;
			case Opcodes.LRETURN:
				return DataType.LONG;
			case Opcodes.FRETURN:
				return DataType.FLOAT;
			case Opcodes.DRETURN:
				return DataType.DOUBLE;
			case Opcodes.ARETURN:
				return DataType.UNKNOWN;
			default:
				return DataType.VOID;
		}
	}
}
