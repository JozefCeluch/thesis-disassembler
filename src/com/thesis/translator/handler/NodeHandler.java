package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import org.objectweb.asm.tree.AbstractInsnNode;

public interface NodeHandler {
	void handle(AbstractInsnNode node) throws IncorrectNodeException;

	interface OnNodeMoveListener {
		void processNode();
	}
}
