package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.common.SignatureVisitor;
import com.thesis.common.Util;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.MethodInsnNode;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MethodInvocationExpression extends Expression {

	protected final String mName;
	protected List<Expression> mArguments;
	protected int mArgumentCount;
	protected String mOwnerClass;
	protected String mCallingMethod;
	protected Expression mOwnerInstance;

	public MethodInvocationExpression(MethodInsnNode instruction, String callingMethod) {
		super(instruction);
		mName = instruction.name;
		SignatureVisitor v = new SignatureVisitor(0, null, null);
		SignatureReader r = new SignatureReader(instruction.desc);
		r.accept(v);
		mType = DataType.getType(v.getReturnType());
		mArgumentCount = v.getArguments().size();
		mArguments = new ArrayList<>();
		mOwnerClass = Util.javaObjectName(Util.getFullClassName(instruction.owner));
		mCallingMethod = callingMethod;
	}

	@Override
	public DataType getType() {
		return mType;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		for(int i = 0; i < mArgumentCount; i++) {
			mArguments.add(0, stack.pop());
		}
		mOwnerInstance = stack.pop();
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (isStatic()) {
			writer.append(mOwnerClass).append('.').write(mName);
		} else {
			if (!isLocalMethod()) {
				mOwnerInstance.write(writer);
				writer.write('.');
			}
			writer.write(mName);
		}
		writeArguments(writer);
	}

	protected void writeArguments(Writer writer) throws IOException {
		writer.write("(");
		for(int i = 0; i < mArguments.size(); i++) {
			writer.append(Util.getCommaIfNeeded(i));
			mArguments.get(i).write(writer);
		}
		writer.write(")");
	}

	private boolean isStatic() {
		return mInstruction.getOpcode() == Opcodes.INVOKESTATIC;
	}

	private boolean isLocalMethod() {
		return mOwnerInstance instanceof PrimaryExpression && ((PrimaryExpression) mOwnerInstance).getValue().toString().equals("this");
	}
}
