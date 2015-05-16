package com.thesis.translator.handler;

import com.thesis.common.Util;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.translator.MethodState;
import org.apache.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.lang.reflect.Field;

public abstract class AbstractHandler implements NodeHandler {

	private static final Logger LOG = Logger.getLogger(AbstractHandler.class);

	protected MethodState mState;
	private OnNodeMovedListener mOnMovedListener;

	public AbstractHandler(MethodState state) {
		mState = state;
	}

	public AbstractHandler(MethodState state, OnNodeMovedListener onMovedListener) {
		mState = state;
		mOnMovedListener = onMovedListener;
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
	}

	protected void nodeMoved() {
		if (mOnMovedListener != null) {
			mOnMovedListener.onNodeMoved();
		}
	}

	protected void checkType(AbstractInsnNode node, Class<? extends AbstractInsnNode> clazz) {
		if (!node.getClass().equals(clazz)) throw new IncorrectNodeException("Incorrect node type, expected: " + clazz.getSimpleName());
	}

	protected String logNode(AbstractInsnNode node) {
		String opCode = Util.getOpcodeString(node.getOpcode());
		StringBuilder fields = new StringBuilder();
		for (Field field : node.getClass().getFields()) {
			if (!(field.getName().contains("INSN") || field.getName().contains("LABEL") || field.getName().contains("FRAME") || field.getName().contains("LINE"))) {
				Object fieldVal = null;
				try {
					fieldVal = field.get(node);
				} catch (IllegalAccessException e) {
					System.out.println(field.getName() + " is inaccessible");
				}
				if (fieldVal == null) {
					continue;
				}
				fields.append(field.getName()).append(" = ").append(fieldVal).append("; ");

			}

		}
		return opCode + " " + fields;
	}
}
