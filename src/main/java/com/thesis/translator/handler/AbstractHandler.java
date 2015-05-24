package com.thesis.translator.handler;

import com.thesis.common.Util;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.Expression;
import com.thesis.expression.JumpExpression;
import com.thesis.expression.TryCatchExpression;
import com.thesis.translator.ExpressionStack;
import com.thesis.translator.MethodState;
import com.thesis.translator.TryCatchManager;
import org.apache.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Abstract implementation of a {@link NodeHandler}
 * <p>
 * Contains helper methods usable in other handlers and logic to create try-catch blocks
 */
public abstract class AbstractHandler implements NodeHandler, MethodState.OnLabelChangeListener {

	private static final Logger LOG = Logger.getLogger(AbstractHandler.class);

	protected MethodState mState;
	private OnNodeMovedListener mOnMovedListener;

	/**
	 * A constructor for the handlers that do not need a {@link OnNodeMovedListener}
	 * @param state method context
	 */
	public AbstractHandler(MethodState state) {
		mState = state;
		mState.setOnLabelChangeListener(this);
	}

	/**
	 * A constructor for the handlers that need a {@link OnNodeMovedListener}
	 * @param state method context
	 * @param onMovedListener configured listener
	 */
	public AbstractHandler(MethodState state, OnNodeMovedListener onMovedListener) {
		mState = state;
		mState.setOnLabelChangeListener(this);
		mOnMovedListener = onMovedListener;
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		//does nothing by default
	}

	@Override
	public void onLabelChange(int newLabel) {
		createTryCatchBlocks(mState);
	}

	/**
	 * Should be called by a handler that moved the node to notify that it was moved
	 */
	protected void nodeMoved() {
		if (mOnMovedListener != null) {
			mOnMovedListener.onNodeMoved();
		}
	}

	/**
	 * Should be called to check if the provided node is of the expected type
	 * @param node node provided to the handler
	 * @param clazz expected type
	 */
	protected void checkType(AbstractInsnNode node, Class<? extends AbstractInsnNode> clazz) {
		if (!node.getClass().equals(clazz)) throw new IncorrectNodeException("Incorrect node type, expected: " + clazz.getSimpleName());
	}

	/**
	 * Creates a textual representation of the nodes' fields
	 * @param node node
	 * @return String with information about the node
	 */
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

	/**
	 * Drives the creation of the try-catch blocks
	 * @param state instance of method state
	 */
	protected void createTryCatchBlocks(MethodState state) {
		if (state.getTryCatchManager().isEmpty()) return;
		List<TryCatchManager.Item> tryCatchItems = state.getTryCatchManager().getTryBlocksLocation(state.getCurrentLabel());
		if (tryCatchItems.isEmpty()) return;
		TryCatchExpression tryCatchExpression = null;
		for(TryCatchManager.Item item : tryCatchItems) {
			prepareTryCatchItem(state, item, tryCatchExpression);
			tryCatchExpression = new TryCatchExpression(item);
		}
		state.getActiveStack().push(tryCatchExpression);
	}

	/**
	 * Reads the nodes that are withing the range of the try-catch items and creates the try-catch expression
	 * @param state method state
	 * @param item TryCatch item that contains information about the location of the try-catch block
	 * @param innerTryCatchBlock try catch block that is enclosed inside the one that is being created
	 */
	private void prepareTryCatchItem(MethodState state, TryCatchManager.Item item, TryCatchExpression innerTryCatchBlock) {
		if (item.getCatchBlockCount() == item.getCatchTypes().size()) return;

		// fill try block
		item.setTryStack(state.startNewStack());
		if (innerTryCatchBlock != null) {
			item.getTryStack().push(innerTryCatchBlock);
		}

		while (!mState.isLabelVisited(item.getTryEndLocation() )) {
			if (state.moveNode() == null) break;
			nodeMoved();
		}
		state.finishStack();
		// ignore repeated finally blocks
		ExpressionStack repeatedFinallyCalls = state.startNewStack();
		while (!item.hasHandlerLabel(state.getCurrentLabel())) {
			if (state.moveNode() == null) break;
			nodeMoved();
		}

		int tryCatchBlockEnd = JumpExpression.NO_DESTINATION;
		if (repeatedFinallyCalls.peek() instanceof JumpExpression) {
			tryCatchBlockEnd = ((JumpExpression) repeatedFinallyCalls.peek()).getJumpDestination();
		}
		state.finishStack();
		// fill catch blocks
		boolean isRethrow = false;

		for (int i = 0; i < item.getHandlerCount(); i++) {
			item.addCatchBlock(state.getCurrentLabel(), state.startNewStack());
			int currentBlockLabel = state.getCurrentLabel();
			boolean reachedEndOfCatchBlock = false;
			while (state.getCurrentLabel() == currentBlockLabel ||
					!(item.hasHandlerLabel(state.getCurrentLabel())
						|| (!isRethrow && state.getTryCatchManager().hasCatchHandlerEnd(state.getCurrentLabel())) // when there is a throw in catch block the try-catch blocks overlap
						|| state.getCurrentLabel() == tryCatchBlockEnd)) {
				if (state.moveNode() == null) break;
				nodeMoved();
				if (currentBlockLabel != state.getCurrentLabel() && state.getTryCatchManager().hasCatchHandlerLocation(state.getCurrentLabel())) {
					reachedEndOfCatchBlock = true;
					isRethrow = true;
				}
				if (hasReachedAnotherHandler(state, item) || hasReachedEndOfCatchBlock(state.getActiveStack().peek(), tryCatchBlockEnd)) {
					reachedEndOfCatchBlock = true;
					break;
				}
			}
			state.finishStack();

			if (!reachedEndOfCatchBlock) {
				state.startNewStack();
				// ignore repeated finally blocks
				while (!item.hasHandlerLabel(state.getCurrentLabel()) && state.getCurrentLabel() != tryCatchBlockEnd){
					if (state.moveNode() == null) break;
					nodeMoved();
				}
				state.finishStack();
			}
		}
	}

	private boolean hasReachedEndOfCatchBlock(Expression topExpression, int tryCatchBlockEnd) {
		return topExpression instanceof JumpExpression && ((JumpExpression) topExpression).getJumpDestination() == tryCatchBlockEnd;
	}

	private boolean hasReachedAnotherHandler(MethodState state, TryCatchManager.Item item) {
		return !item.getCatchLocations().contains(state.getCurrentLabel()) && state.getTryCatchManager().hasCatchBlockStart(state.getCurrentLabel());
	}
}
