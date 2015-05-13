package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.translator.MethodState;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LineNumberNode;

public class LineNumberNodeHandler extends AbstractHandler {

	public LineNumberNodeHandler(MethodState state) {
		super(state);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		checkType(node, LineNumberNode.class);

		mState.setCurrentLine(((LineNumberNode)node).line);
	}
}
