package com.thesis.translator.handler;

import com.thesis.common.DataType;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.ArrayCreationExpression;
import com.thesis.expression.PrimaryExpression;
import com.thesis.expression.stack.ExpressionStack;
import com.thesis.translator.MethodState;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;

public class IntInsnNodeHandler extends AbstractHandler {

	public IntInsnNodeHandler(MethodState state) {
		super(state);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		checkType(node, IntInsnNode.class);

		ExpressionStack stack = mState.getActiveStack();
		int opCode = node.getOpcode();
		switch (opCode) {
			case Opcodes.BIPUSH:
				stack.push(new PrimaryExpression(opCode, ((IntInsnNode) node).operand, DataType.BYTE));
				break;
			case Opcodes.SIPUSH:
				stack.push(new PrimaryExpression(opCode, ((IntInsnNode) node).operand, DataType.SHORT));
				break;
			case Opcodes.NEWARRAY:
				stack.push(new ArrayCreationExpression(opCode, ((IntInsnNode) node).operand));
				break;
		}
	}
}
