package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.translator.ExpressionStack;
import com.thesis.translator.MethodState;
import org.apache.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

import java.util.List;

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
		mState.setCurrentLabel(stack.getLabelId(((LabelNode)node).getLabel()));

//		createTryCatchBlocks(mState);
		LOG.debug("LABEL: " + ((LabelNode) node).getLabel() + " L" + stack.getLabel());
	}

//	public void createTryCatchBlocks(MethodState state) {
//		if (mTryCatchManager.isEmpty()) return;
//		List<TryCatchManager.Item> tryCatchItems = mTryCatchManager.getTryBlocksLocation(state.getCurrentLabel());
//		if (tryCatchItems.isEmpty()) return;
//
//		TryCatchExpression tryCatchExpression = null;
//		for(TryCatchManager.Item item : tryCatchItems) {
//			prepareTryCatchItem(state, item, tryCatchExpression);
//			tryCatchExpression = new TryCatchExpression(item);
//		}
//		state.getActiveStack().push(tryCatchExpression);
//	}
//
//	private void prepareTryCatchItem(MethodState state, TryCatchManager.Item item, TryCatchExpression innerTryCatchBlock) {
//		if (item.getCatchBlockCount() == item.getCatchTypes().size()) return;
//
//		// fill try block
//		item.setTryStack(state.startNewStack());
//		if (innerTryCatchBlock != null) {
//			item.getTryStack().push(innerTryCatchBlock);
//		}
//
//		while (item.getTryEndLocation() != state.getCurrentLabel()) {
//			if (state.moveNode() == null) break;
//			nodeMoved();
//		}
//		state.finishStack();
//		// ignore repeated finally blocks
//		ExpressionStack repeatedFinallyCalls = state.startNewStack();
//		while (!item.hasHandlerLabel(state.getCurrentLabel())) {
//			if (state.moveNode() == null) break;
//			nodeMoved();
//		}
//
//		int tryCatchBlockEnd = JumpExpression.NO_DESTINATION;
//		if (repeatedFinallyCalls.peek() instanceof JumpExpression) {
//			tryCatchBlockEnd = ((JumpExpression) repeatedFinallyCalls.peek()).getJumpDestination();
//		}
//		state.finishStack();
//		// fill catch blocks
//		for (int i = 0; i < item.getHandlerCount(); i++) {
//			item.addCatchBlock(state.getCurrentLabel(), state.startNewStack());
//			int currentBlockLabel = state.getCurrentLabel();
//
//			while (state.getCurrentLabel() == currentBlockLabel || !(item.hasHandlerLabel(state.getCurrentLabel())
//					|| mTryCatchManager.hasCatchHandlerEnd(state.getCurrentLabel()) || state.getCurrentLabel() == tryCatchBlockEnd)) {
//				if (state.moveNode() == null) break;
//				nodeMoved();
//			}
//			state.finishStack();
//
//			state.startNewStack();
//			// ignore repeated finally blocks
//			while (!(item.hasHandlerLabel(state.getCurrentLabel()) || state.getCurrentLabel() == tryCatchBlockEnd)) {
//				if (state.moveNode() == null) break;
//				nodeMoved();
//			}
//			state.finishStack();
//		}
//	}
}
