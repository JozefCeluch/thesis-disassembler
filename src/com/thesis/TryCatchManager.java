package com.thesis;

import com.thesis.expression.ExpressionStack;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.util.ArrayList;
import java.util.List;

public class TryCatchManager {

	private List<TryCatchItem> mTryCatchItems;
	private List<TryCatchItem> mFinallyItems;

	/**
	 * Use the newInstance method
	 */
	private TryCatchManager() {
	}

	public void setTryCatchItems(List<TryCatchItem> tryCatchItems) {
		mTryCatchItems = tryCatchItems;
	}

	public void setFinallyItems(List<TryCatchItem> finallyItems) {
		mFinallyItems = finallyItems;
	}

	public boolean isEmpty() {
		return mTryCatchItems == null || mFinallyItems == null || (mTryCatchItems.isEmpty() && mFinallyItems.isEmpty());
	}

	public List<TryCatchItem> getItemsWithStartId(int labelId) {
		List<TryCatchItem> result = new ArrayList<>();
		for (TryCatchItem item : mTryCatchItems) {
			if (item.getStartId() == labelId) result.add(item);
		}
		return result;
	}

	public TryCatchItem getItemWithEnd(int labelId) {
		for (TryCatchItem item : mTryCatchItems) {
			if (item.getEndId() == labelId) return item;
		}
		return null;
	}

	public static TryCatchManager newInstance(List tryCatchBlocks, ExpressionStack stack) {
		TryCatchManager manager = new TryCatchManager();
		List<TryCatchItem> tryCatchItems = new ArrayList<>();
		List<TryCatchItem> finallyItems = new ArrayList<>();
		if (tryCatchBlocks != null) {
			for (Object block : tryCatchBlocks) {
				TryCatchBlockNode node = (TryCatchBlockNode) block;
				TryCatchItem item = new TryCatchItem(stack.getLabelId(node.start.getLabel()), stack.getLabelId(node.end.getLabel()),
						stack.getLabelId(node.handler.getLabel()), node.type);
				if (node.type == null) {
					addNewItemToList(finallyItems, item);
				} else {
					addNewItemToList(tryCatchItems, item);
				}
			}
		}
		manager.setTryCatchItems(tryCatchItems);
		manager.setFinallyItems(finallyItems);
		return manager;
	}

	private static void addNewItemToList(List<TryCatchItem> tryCatchItems, TryCatchItem item) {
		boolean foundMatch = false;
		for (TryCatchItem tryCatchItem : tryCatchItems) {
			if (tryCatchItem.matches(item)) {
				foundMatch = true;
				tryCatchItem.addHandlers(item.getHandlerLocations(), item.getHandlerTypes());
				break;
			}
		}
		if (!foundMatch) {
			tryCatchItems.add(item);
		}
	}

	public boolean isDefaultHandlerEnd(int label) {
		for (TryCatchItem item : mFinallyItems) {
			if (item.getEndId() == label) return true;
		}
		return false;
	}
}
