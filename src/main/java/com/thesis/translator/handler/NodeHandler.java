package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Node handler used by the {@link com.thesis.translator.InstructionTranslator}
 * <p>
 * A general interface that can process any {@link  AbstractInsnNode}
 */
public interface NodeHandler {

	/**
	 * @param node handled by this handler
	 * @throws IncorrectNodeException in case this handler cannot process this node
	 */
	void handle(AbstractInsnNode node) throws IncorrectNodeException;

	/**
	 * Listener that is can be used to notify that the currently processed node has moved to the next one
	 */
	interface OnNodeMovedListener {
		void onNodeMoved();
	}
}
