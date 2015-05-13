package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.ArrayCreationExpression;
import com.thesis.expression.InstanceOfExpression;
import com.thesis.expression.NewExpression;
import com.thesis.translator.ExpressionStack;
import com.thesis.translator.MethodState;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * NEW, ANEWARRAY, CHECKCAST or INSTANCEOF
 */
public class TypeInsnNodeHandler extends AbstractHandler {

	public TypeInsnNodeHandler(MethodState state) {
		super(state);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		checkType(node, TypeInsnNode.class);

		ExpressionStack stack = mState.getActiveStack();
		int opCode = node.getOpcode();
		String desc = ((TypeInsnNode) node).desc;
		if (opCode == Opcodes.NEW) {
			stack.push(new NewExpression(opCode, desc));
		}
		if (opCode == Opcodes.INSTANCEOF) {
			stack.push(new InstanceOfExpression(opCode, desc));
		}
		if (opCode == Opcodes.ANEWARRAY) {
			stack.push(new ArrayCreationExpression(opCode, desc));
		}
	}
}
