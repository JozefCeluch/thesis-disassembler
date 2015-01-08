package com.thesis.expression;

import com.thesis.common.Util;
import org.objectweb.asm.tree.MethodInsnNode;

import java.io.IOException;
import java.io.Writer;

public class ConstructorInvocationExpression extends MethodInvocationExpression {

	protected NewExpression mNewExpression;

	public ConstructorInvocationExpression(MethodInsnNode instruction, String callingMethod) {
		super(instruction, callingMethod);
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
		if (Util.isConstructor(mCallingMethod)) {
			writer.write("super");
		} else {
			writer.write(mOwnerClass);
		}
		writeArguments(writer);
	}
}
