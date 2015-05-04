package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.JumpExpression;
import com.thesis.expression.TryCatchExpression;
import com.thesis.expression.UnconditionalJump;
import com.thesis.expression.stack.ExpressionStack;
import com.thesis.translator.MethodState;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

import java.util.List;

public class LabelNodeHandler extends AbstractHandler {

	private TryCatchManager mTryCatchManager;

	public LabelNodeHandler(MethodState state, List tryCatchBlocks, OnNodeMoveListener moveListener) {
		super(state, moveListener);
		mTryCatchManager = TryCatchManager.newInstance(tryCatchBlocks, mState.getFinalStack());;
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		checkType(node, LabelNode.class);

		ExpressionStack stack = mState.getActiveStack();
		mState.setCurrentLabel(stack.getLabelId(((LabelNode)node).getLabel()));
		stack.setLabel(mState.getCurrentLabel());
		System.out.println("LABEL: " + "L" + mState.getCurrentLabel());

		createTryCatchBlocks(mState);
	}

	public void createTryCatchBlocks(MethodState state) {
		if (mTryCatchManager.isEmpty()) return;
		List<TryCatchManager.Item> tryCatchItems = mTryCatchManager.getItemsWithStartId(state.getCurrentLabel());
		if (tryCatchItems.isEmpty()) return;

		TryCatchExpression tryCatchExpression = null;
		for(TryCatchManager.Item item : tryCatchItems) {
			prepareTryCatchItem(state, item, tryCatchExpression);
			tryCatchExpression = new TryCatchExpression(item);
		}
		state.getActiveStack().push(tryCatchExpression);
	}

	private void prepareTryCatchItem(MethodState state, TryCatchManager.Item item, TryCatchExpression innerTryCatchBlock) {
		if (item.getCatchBlockCount() == item.getHandlerTypes().size()) return;

		// fill try block
		item.setTryStack(state.startNewStack());
		if (innerTryCatchBlock != null) {
			item.getTryStack().push(innerTryCatchBlock);
		}

		while (item.getEndId() != state.getCurrentLabel()) {
			state.moveNode();
			nodeMoved();
		}
		state.finishStack();
		// ignore repeated finally blocks
		ExpressionStack repeatedFinallyCalls = state.startNewStack();
		while (!item.hasHandlerLabel(state.getCurrentLabel())) {
			state.moveNode();
			nodeMoved();
		}

		int tryCatchBlockEnd = JumpExpression.NO_DESTINATION;
		if (repeatedFinallyCalls.peek() instanceof UnconditionalJump) {
			tryCatchBlockEnd = ((UnconditionalJump) repeatedFinallyCalls.peek()).getJumpDestination();
		}
		state.finishStack();
		// fill catch blocks
		for (int i = 0; i < item.getHandlerCount(); i++) {
			item.addCatchBlock(state.getCurrentLabel(), state.startNewStack());
			int currentBlockLabel = state.getCurrentLabel();
			if (item.getHandlerType(currentBlockLabel) == null) {
				item.setHasFinallyBlock(true);
				item.setFinallyBlockStart(currentBlockLabel);
			}
			while (state.getCurrentLabel() == currentBlockLabel || !(item.hasHandlerLabel(state.getCurrentLabel())
					|| mTryCatchManager.hasCatchHandlerEnd(state.getCurrentLabel()) || state.getCurrentLabel() == tryCatchBlockEnd)) {
				state.moveNode();
				nodeMoved();
			}
			state.finishStack();

			state.startNewStack();
			// ignore repeated finally blocks
			while (!(item.hasHandlerLabel(state.getCurrentLabel()) || state.getCurrentLabel() == tryCatchBlockEnd)) {
				state.moveNode();
				nodeMoved();
			}
			state.finishStack();
		}
	}
}
