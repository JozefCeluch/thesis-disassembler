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

	private final String mName;
	private List<Expression> mArguments;
	private int mArgumentCount;
	private String mOwnerClass;
	private String mCallingMethod;
	private NewExpression mPreviousExp;
	private PrimaryExpression mOwnerInstance;

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
		Expression stackTop = stack.peek();
		if (stackTop instanceof NewExpression) {
			mPreviousExp = (NewExpression) stack.pop();
		} else if (stackTop instanceof PrimaryExpression) {
			mOwnerInstance = (PrimaryExpression) stackTop;
		}
	}

	@Override
	public void afterPush(ExpressionStack stack) {
		if (mPreviousExp != null) {
			mPreviousExp.setExpression(stack.pop());
			stack.push(mPreviousExp);
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (isConstructor()) {
			writeConstructorInvocation(writer);
		} else if (isStatic()) {
			writer.append(mOwnerClass).append('.').write(mName);
		} else {
			if (mOwnerInstance.getValue() != null && !mOwnerInstance.getValue().toString().equals("this")) {
				mOwnerInstance.write(writer);
				writer.write('.');
			}
			writer.write(mName);
		}
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

	private void writeConstructorInvocation(Writer writer) throws IOException {
		if ("<init>".equals(mCallingMethod)) {
			writer.write("super");
		} else {
			writer.write(mOwnerClass);
		}
	}

	private boolean isConstructor() {
		return "<init>".equals(mName);
	}
}
