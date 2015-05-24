package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.LambdaExpression;
import com.thesis.translator.MethodState;
import org.apache.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;

/**
 * Handles the {@link InvokeDynamicInsnNode}
 * <p>
 * instructions:
 * INVOKEDYNAMIC
 */
public class InvokeDynamicInsnNodeHandler extends AbstractHandler {
	private static final Logger LOG = Logger.getLogger(InvokeDynamicInsnNodeHandler.class);

	public InvokeDynamicInsnNodeHandler(MethodState state) {
		super(state);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		LOG.debug(logNode(node));
		checkType(node, InvokeDynamicInsnNode.class);
		InvokeDynamicInsnNode invokeNode = (InvokeDynamicInsnNode) node;
		mState.getActiveStack().push(new LambdaExpression(invokeNode.name, invokeNode.desc, invokeNode.bsm, invokeNode.bsmArgs));
	}
}
