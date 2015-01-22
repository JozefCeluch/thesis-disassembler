package com.thesis;

import com.thesis.expression.ExpressionStack;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.util.ArrayList;
import java.util.List;

public class TryCatchManager {

	private List<TryCatchItem> mTryCatchItems;

	/**
	 * Use the newInstance method
	 */
	private TryCatchManager() {
	}

	public void setTryCatchItems(List<TryCatchItem> tryCatchItems) {
		mTryCatchItems = tryCatchItems;
	}

	public boolean isEmpty() {
		return mTryCatchItems == null || mTryCatchItems.isEmpty();
	}

	public List<TryCatchItem> getItemsWithStartId(int labelId) {
		List<TryCatchItem> result = new ArrayList<>();
		for (TryCatchItem item : mTryCatchItems) {
			if (item.getStartId() == labelId) result.add(item);
		}
		return result;
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
				addNewItemToList(tryCatchItems, item);
			}
		}
		manager.setTryCatchItems(tryCatchItems);
		return manager;
	}

	private static void addNewItemToList(List<TryCatchItem> tryCatchItems, TryCatchItem item) {
		boolean foundMatch = false;
		boolean isCatchBlockHandler = false;
		for (TryCatchItem tryCatchItem : tryCatchItems) {
			if (tryCatchItem.matches(item)) {
				foundMatch = true;
				tryCatchItem.addHandlers(item.getHandlerLocations(), item.getHandlerTypes());
			}
			if (tryCatchItem.getHandlerLocations().contains(item.getStartId())) {
				isCatchBlockHandler = true;
			}
			if (foundMatch && isCatchBlockHandler) break;
		}
		if (!foundMatch && !isCatchBlockHandler) {
			tryCatchItems.add(item);
		}
	}
}
