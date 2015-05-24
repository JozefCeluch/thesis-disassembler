package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.translator.ExpressionStack;
import com.thesis.translator.MethodState;
import org.apache.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * Handles the {@link LabelNode}
 */
public class LabelNodeHandler extends AbstractHandler {
	private static final Logger LOG = Logger.getLogger(LabelNodeHandler.class);

	public LabelNodeHandler(MethodState state, OnNodeMovedListener onMovedListener) {
		super(state, onMovedListener);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);

		checkType(node, LabelNode.class);

		ExpressionStack stack = mState.getActiveStack();
		mState.updateCurrentLabel(stack.getLabelId(((LabelNode) node).getLabel()));

		LOG.debug("LABEL: " + ((LabelNode) node).getLabel() + " L" + stack.getLabel());
	}
}
