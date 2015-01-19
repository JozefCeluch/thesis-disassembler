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

	public List<TryCatchItem> getTryCatchItems() {
		return mTryCatchItems;
	}

	public void setTryCatchItems(List<TryCatchItem> tryCatchItems) {
		mTryCatchItems = tryCatchItems;
	}

	public List<TryCatchItem> getFinallyItems() {
		return mFinallyItems;
	}

	public void setFinallyItems(List<TryCatchItem> finallyItems) {
		mFinallyItems = finallyItems;
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
				tryCatchItem.addHandlers(item.getHandlerLocations());
				break;
			}
		}
		if (!foundMatch) {
			tryCatchItems.add(item);
		}
	}
}
