package com.thesis.translator.handler;

import com.thesis.common.Util;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.translator.MethodState;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.lang.reflect.Field;

public abstract class AbstractHandler implements NodeHandler {

	protected MethodState mState;
	private OnNodeMoveListener mMoveListener;

	public AbstractHandler(MethodState state) {
		mState = state;
	}

	public AbstractHandler(MethodState state, OnNodeMoveListener moveListener) {
		mState = state;
		mMoveListener = moveListener;
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		printNodeInfo(node, mState);
	}

	public void setMoveListener(OnNodeMoveListener moveListener) {
		mMoveListener = moveListener;
	}

	protected void nodeMoved() {
		if (mMoveListener != null) {
			mMoveListener.processNode();
		}
	}

	protected void checkType(AbstractInsnNode node, Class<? extends AbstractInsnNode> clazz) throws IncorrectNodeException {
		if (!node.getClass().equals(clazz)) throw new IncorrectNodeException("Incorrect node type, expected: " + clazz.getSimpleName());
	}

	public static void printNodeInfo(AbstractInsnNode node, MethodState state) {
		String opCode = Util.getOpcodeString(node.getOpcode());
		if (opCode.isEmpty()) return;
		String fields = "";
		for (Field field : node.getClass().getFields()) {
			if (!(field.getName().contains("INSN") || field.getName().contains("LABEL") || field.getName().contains("FRAME") || field.getName().contains("LINE"))) {
				try {
					fields += field.getName() + " = " + field.get(node);
					fields += "; ";
				} catch (IllegalAccessException e) {
					System.out.println(field.getName() + " is inaccessible");
				}
			}

		}
		System.out.println("STACK: " + state.getActiveStack().size());
		String result = "code: " + opCode + " " + fields;
		result += "\nCURRENT LABEL: " + state.getCurrentLabel() + ",  STACK: " + state.getActiveStack().getLabel();
		System.out.println(result);
	}
}
