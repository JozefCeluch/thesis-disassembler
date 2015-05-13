package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.ArrayCreationExpression;
import com.thesis.translator.MethodState;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;

/**
 * MULTIANEWARRAY
 */
public class MultiANewArrayInsnNodeHandler extends AbstractHandler {

	public MultiANewArrayInsnNodeHandler(MethodState state) {
		super(state);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		checkType(node, MultiANewArrayInsnNode.class);

		String desc = ((MultiANewArrayInsnNode)node).desc;
		int dims = ((MultiANewArrayInsnNode)node).dims;
		mState.getActiveStack().push(new ArrayCreationExpression(node.getOpcode(), desc, dims));
	}
}
