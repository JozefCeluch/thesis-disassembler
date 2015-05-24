package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.translator.MethodState;
import org.apache.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;

/**
 * Handles the {@link FrameNode}
 */
public class FrameNodeHandler extends AbstractHandler {

	private static final Logger LOG = Logger.getLogger(FrameNodeHandler.class);

	public FrameNodeHandler(MethodState state) {
		super(state);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		LOG.debug(logNode(node));
		checkType(node, FrameNode.class);

		mState.setFrameLabel(mState.getCurrentLabel());
	}
}
