package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.translator.MethodState;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;

import java.util.Arrays;

public class FrameNodeHandler extends AbstractHandler {

	public FrameNodeHandler(MethodState state) {
		super(state);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		checkType(node, FrameNode.class);

		System.out.println("FRAME:");
		System.out.println("local: " + Arrays.deepToString(((FrameNode)node).local.toArray()));
		System.out.println("stack: " + Arrays.deepToString(((FrameNode)node).stack.toArray()));

		mState.setFrameLabel(mState.getCurrentLabel());
	}
}
