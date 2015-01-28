package com.thesis;

import com.thesis.expression.ExpressionStack;
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

	public void setTryCatchItems(List<TryCatchItem> tryCatchItems) {
		mTryCatchItems = tryCatchItems;
	}

	public void setCatchBlockHandlers(List<TryCatchItem> catchBlockHandlers) {
		mCatchBlockHandlers = catchBlockHandlers;
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

	public boolean hasCatchHandlerEnd(int labelId) {
		for(TryCatchItem item : mCatchBlockHandlers) {
			if (item.getEndId() == labelId) return true;
		}
		return false;
	}

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
}
