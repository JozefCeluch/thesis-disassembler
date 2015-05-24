package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.common.SignatureVisitor;
import com.thesis.common.Util;
import com.thesis.translator.ExpressionStack;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Expression that represents a call to any method
 *<p>
 * (except for constructor {@link ConstructorInvocationExpression} )
 * used for the following instructions:
 * INVOKEVIRTUAL, INVOKESPECIAL (not constructor), INVOKESTATIC, INVOKEINTERFACE
 */
public class MethodInvocationExpression extends Expression {

	protected final String mName;
	protected List<Expression> mArguments;
	protected int mArgumentCount;
	protected DataType mOwnerClass;
	protected List<DataType> mArgTypes;
	protected String mCallingMethod;
	protected Expression mOwnerInstance;

	public MethodInvocationExpression(int opCode, String name, String desc, String owner, String callingMethod) {
		super(opCode);
		mName = name;
		SignatureVisitor v = new SignatureVisitor(0, null, null);
		SignatureReader r = new SignatureReader(desc);
		r.accept(v);
		mType = DataType.getTypeFromObject(v.getReturnType());
		mArgTypes = new ArrayList<>();
		for (Type argType: Type.getArgumentTypes(desc)) {
			mArgTypes.add(DataType.getType(argType));
		}
		mArgumentCount = v.getArguments().size();
		mArguments = new ArrayList<>();
		mOwnerClass = DataType.getType(Type.getObjectType(owner));
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
		if (!isStatic()) {
			mOwnerInstance = stack.pop();
			if (!mOwnerInstance.hasType()) {
				mOwnerInstance.setType(mOwnerClass);
			}
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (isStatic()) {
			writer.append(mOwnerClass.print(isStatic())).append('.').write(mName);
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
			Expression arg = mArguments.get(i);
			setArgumentCastType(i, arg);
			arg.write(writer);
		}
		writer.write(")");
	}

	private void setArgumentCastType(int argumentPosition, Expression arg) {
		DataType typeFromDesc = mArgTypes.get(argumentPosition);
		if (typeFromDesc == null || DataType.UNKNOWN.equals(typeFromDesc)) {
			return;
		}
		if (!typeFromDesc.equalsWithoutGeneric(arg.getType()) && !isValueNull(arg)) {
			arg.setCastType(typeFromDesc);
		}
	}

	private boolean isValueNull(Expression exp) {
		return exp instanceof ConstantPrimaryExpression && "null".equals(((ConstantPrimaryExpression) exp).getValue());
	}

	private boolean isStatic() {
		return mOpCode == Opcodes.INVOKESTATIC;
	}

	private boolean isLocalMethod() {
		return mOwnerInstance instanceof PrimaryExpression && ((PrimaryExpression) mOwnerInstance).getValue().toString().equals("this");
	}
}
