package com.thesis.translator;

import com.thesis.expression.ConditionalExpression;
import com.thesis.expression.TryCatchExpression;
import com.thesis.expression.UnconditionalJump;
import com.thesis.expression.stack.ExpressionStack;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.util.ArrayList;
import java.util.List;

public class TryCatchManager {

	private List<TryCatchItem> mTryCatchItems;
	private List<TryCatchItem> mCatchBlockHandlers;

	/**
	 * Use the newInstance method
	 */
	private TryCatchManager() {
	}

	//region INIT
	public static TryCatchManager newInstance(List tryCatchBlocks, ExpressionStack stack) {
		TryCatchManager manager = new TryCatchManager();
		List<TryCatchItem> tryCatchItems = new ArrayList<>();
		List<TryCatchItem> catchBlockHandlers = new ArrayList<>();
		if (tryCatchBlocks != null) {
			for (Object block : tryCatchBlocks) {
				TryCatchBlockNode node = (TryCatchBlockNode) block;
				TryCatchItem item = new TryCatchItem(stack.getLabelId(node.start.getLabel()), stack.getLabelId(node.end.getLabel()),
						stack.getLabelId(node.handler.getLabel()), node.type);
				addNewItemToList(tryCatchItems, catchBlockHandlers, item);
			}
		}
		manager.setTryCatchItems(tryCatchItems);
		manager.setCatchBlockHandlers(catchBlockHandlers);
		return manager;
	}

	private static void addNewItemToList(List<TryCatchItem> tryCatchItems, List<TryCatchItem> catchBlockHandlers, TryCatchItem newItem) {
		boolean foundMatch = false;
		boolean isCatchBlockHandler = false;
		for (TryCatchItem tryCatchItem : tryCatchItems) {
			if (tryCatchItem.matches(newItem)) {
				foundMatch = true;
				tryCatchItem.addHandlers(newItem.getHandlerLocations(), newItem.getHandlerTypes());
			}
			if (tryCatchItem.getHandlerLocations().contains(newItem.getStartId())) {
				isCatchBlockHandler = true;
			}
			if (foundMatch && isCatchBlockHandler) break;
		}
		if (!foundMatch && !isCatchBlockHandler) {
			tryCatchItems.add(newItem);
		}

		if (isCatchBlockHandler && !newItem.getHandlerLocations().contains(newItem.getStartId())) {
			catchBlockHandlers.add(newItem);
		}
	}
	//endregion

	public void createTryCatchBlocks(MethodState state) {
		if (isEmpty()) return;
		List<TryCatchItem> tryCatchItems = getItemsWithStartId(state.getCurrentLabel());
		if (tryCatchItems.isEmpty()) return;

		TryCatchExpression tryCatchExpression = null;
		for(TryCatchItem item : tryCatchItems) {
			prepareTryCatchItem(state, item, tryCatchExpression);
			tryCatchExpression = new TryCatchExpression(item);
		}
		state.getActiveStack().push(tryCatchExpression);
	}

	private void prepareTryCatchItem(MethodState state, TryCatchItem tryCatchItem, TryCatchExpression innerTryCatchBlock) {
		if (tryCatchItem.getCatchBlockCount() == tryCatchItem.getHandlerTypes().size()) return;

		// fill try block
		tryCatchItem.setTryStack(state.startNewStack());
		if (innerTryCatchBlock != null) {
			tryCatchItem.getTryStack().push(innerTryCatchBlock);
		}

		while (tryCatchItem.getEndId() != state.getCurrentLabel()) {
			state.moveNode();
			state.getTranslator().pushNodeToStackAsExpression(state.getCurrentNode());
		}
		state.finishStack();
		// ignore repeated finally blocks
		ExpressionStack repeatedFinallyCalls = state.startNewStack();
		while (!tryCatchItem.hasHandlerLabel(state.getCurrentLabel())) {
			state.moveNode();
			state.getTranslator().pushNodeToStackAsExpression(state.getCurrentNode());
		}

		int tryCatchBlockEnd = ConditionalExpression.NO_DESTINATION;
		if (repeatedFinallyCalls.peek() instanceof UnconditionalJump) {
			tryCatchBlockEnd = ((UnconditionalJump) repeatedFinallyCalls.peek()).getJumpDestination();
		}
		state.finishStack();
		// fill catch blocks
		for (int i = 0; i < tryCatchItem.getHandlerCount(); i++) {
			tryCatchItem.addCatchBlock(state.getCurrentLabel(), state.startNewStack());
			int currentBlockLabel = state.getCurrentLabel();
			if (tryCatchItem.getHandlerType(currentBlockLabel) == null) {
				tryCatchItem.setHasFinallyBlock(true);
				tryCatchItem.setFinallyBlockStart(currentBlockLabel);
			}
			while (state.getCurrentLabel() == currentBlockLabel || !(tryCatchItem.hasHandlerLabel(state.getCurrentLabel())
					|| hasCatchHandlerEnd(state.getCurrentLabel()) || state.getCurrentLabel() == tryCatchBlockEnd)) {
				state.moveNode();
				state.getTranslator().pushNodeToStackAsExpression(state.getCurrentNode());
			}
			state.finishStack();

			state.startNewStack();
			// ignore repeated finally blocks
			while (!(tryCatchItem.hasHandlerLabel(state.getCurrentLabel()) || state.getCurrentLabel() == tryCatchBlockEnd)) {
				state.moveNode();
				state.getTranslator().pushNodeToStackAsExpression(state.getCurrentNode());
			}
			state.finishStack();
		}
	}

	private void setTryCatchItems(List<TryCatchItem> tryCatchItems) {
		mTryCatchItems = tryCatchItems;
	}

	private void setCatchBlockHandlers(List<TryCatchItem> catchBlockHandlers) {
		mCatchBlockHandlers = catchBlockHandlers;
	}

	private boolean isEmpty() {
		return mTryCatchItems == null || mTryCatchItems.isEmpty();
	}

	private List<TryCatchItem> getItemsWithStartId(int labelId) {
		List<TryCatchItem> result = new ArrayList<>();
		for (TryCatchItem item : mTryCatchItems) {
			if (item.getStartId() == labelId) result.add(item);
		}
		return result;
	}

	private boolean hasCatchHandlerEnd(int labelId) {
		for(TryCatchItem item : mCatchBlockHandlers) {
			if (item.getEndId() == labelId) return true;
		}
		return false;
	}
}
