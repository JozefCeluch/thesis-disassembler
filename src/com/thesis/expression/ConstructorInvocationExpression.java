package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import com.thesis.translator.ExpressionStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Expression that represents a constructor invocation
 *<p>
 * a special case of {@link MethodInvocationExpression}
 * used for the INVOKESPECIAL instruction but only in the case that it calls the special init method
 *
 * in case when constructor is invoked directly via the new keyword it is wrapped into the {@link NewExpression}
 */
public class ConstructorInvocationExpression extends MethodInvocationExpression {

	protected NewExpression mNewExpression;
	protected DataType mContainingClass;

	public ConstructorInvocationExpression(int opCode, String name, String desc, String owner, String callingMethod, DataType containingClass) {
		super(opCode, name, desc, owner, callingMethod);
		mContainingClass = containingClass;
	}

	@Override
	public void prepareForStack(ExpressionStack stack) {
		for(int i = 0; i < mArgumentCount; i++) {
			mArguments.add(0, stack.pop());
		}
		Expression stackTop = stack.pop();
		if (stackTop instanceof NewExpression) {
			mNewExpression = (NewExpression) stackTop;
		}
	}

	@Override
	public void afterPush(ExpressionStack stack) {
		if (mNewExpression != null) {
			mNewExpression.setExpression(stack.pop());
			stack.push(mNewExpression);
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (!Util.isConstructor(mCallingMethod) || mNewExpression != null) {
			writer.write(mOwnerClass.print());
		} else if (mContainingClass.equals(mOwnerClass)) {
			writer.write("this");
		} else {
			writer.write("super");
		}
		writeArguments(writer);
	}
}
