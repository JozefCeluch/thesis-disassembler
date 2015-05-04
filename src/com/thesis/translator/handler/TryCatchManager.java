package com.thesis.translator.handler;

import com.thesis.translator.ExpressionStack;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TryCatchManager {

	private List<Item> mItems;
	private List<Item> mCatchBlockHandlers;

	/**
	 * Use the newInstance method
	 */
	private TryCatchManager() {
	}

	//region INIT
	public static TryCatchManager newInstance(List tryCatchBlocks, ExpressionStack stack) {
		TryCatchManager manager = new TryCatchManager();
		List<Item> tryCatchItems = new ArrayList<>();
		List<Item> catchBlockHandlers = new ArrayList<>();
		if (tryCatchBlocks != null) {
			for (Object block : tryCatchBlocks) {
				TryCatchBlockNode node = (TryCatchBlockNode) block;
				Item item = new Item(stack.getLabelId(node.start.getLabel()), stack.getLabelId(node.end.getLabel()),
						stack.getLabelId(node.handler.getLabel()), node.type);
				addNewItemToList(tryCatchItems, catchBlockHandlers, item);
			}
		}
		manager.setItems(tryCatchItems);
		manager.setCatchBlockHandlers(catchBlockHandlers);
		return manager;
	}

	private static void addNewItemToList(List<Item> tryCatchItems, List<Item> catchBlockHandlers, Item newItem) {
		boolean foundMatch = false;
		boolean isCatchBlockHandler = false;
		for (Item item : tryCatchItems) {
			if (item.matches(newItem)) {
				foundMatch = true;
				item.addHandlers(newItem.getHandlerLocations(), newItem.getHandlerTypes());
			}
			if (item.getHandlerLocations().contains(newItem.getStartId())) {
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

	private void setItems(List<Item> items) {
		mItems = items;
	}

	private void setCatchBlockHandlers(List<Item> catchBlockHandlers) {
		mCatchBlockHandlers = catchBlockHandlers;
	}

	public boolean isEmpty() {
		return mItems == null || mItems.isEmpty();
	}

	public List<Item> getItemsWithStartId(int labelId) {
		List<Item> result = new ArrayList<>();
		for (Item item : mItems) {
			if (item.getStartId() == labelId) result.add(item);
		}
		return result;
	}

	public boolean hasCatchHandlerEnd(int labelId) {
		for(Item item : mCatchBlockHandlers) {
			if (item.getEndId() == labelId) return true;
		}
		return false;
	}

	public static class Item {

		private int mStartId;
		private int mEndId;
		private List<Integer> mHandlerLocations = new ArrayList<>();
		private Map<Integer, String> mHandlerTypes = new HashMap<>(); // labelId, type
		private ExpressionStack mTryStack;
		private Map<Integer, ExpressionStack> mCatchStacks = new HashMap<>(); //labelId, stack
		private boolean mHasFinallyBlock;
		private int mFinallyBlockStart;

		public Item(int startId, int endId, int handlerId, String exception) {
			mStartId = startId;
			mEndId = endId;
			mHandlerLocations.add(handlerId);
			mHandlerTypes.put(handlerId, exception);
		}

		public int getStartId() {
			return mStartId;
		}

		public int getEndId() {
			return mEndId;
		}

		public ExpressionStack getTryStack() {
			return mTryStack;
		}

		public void setTryStack(ExpressionStack tryStack) {
			mTryStack = tryStack;
		}

		public boolean hasFinallyBlock() {
			return mHasFinallyBlock;
		}

		public void setHasFinallyBlock(boolean hasFinallyBlock) {
			this.mHasFinallyBlock = hasFinallyBlock;
		}

		public int getFinallyBlockStart() {
			return mFinallyBlockStart;
		}

		public void setFinallyBlockStart(int finallyBlockStart) {
			mFinallyBlockStart = finallyBlockStart;
		}

		public Map<Integer, String> getHandlerTypes() {
			return mHandlerTypes;
		}

		public List<Integer> getHandlerLocations() {
			return mHandlerLocations;
		}

		public int getHandlerCount() {
			return mHandlerTypes.size();
		}

		public int getCatchBlockCount() {
			return mCatchStacks.size();
		}

		public void addHandlers(List<Integer> handlerLocations, Map<Integer,String> handlers) {
			mHandlerLocations.addAll(handlerLocations);
			mHandlerTypes.putAll(handlers);
		}

		public void removeHandler(int location) {
			mHandlerLocations.remove(mHandlerLocations.indexOf(location));
			mCatchStacks.remove(location);
		}

		public boolean matches(Item other) {
			return this.mStartId == other.mStartId && this.mEndId == other.mEndId;
		}

		public boolean hasHandlerLabel(int label) {
			for(int key : mHandlerTypes.keySet()) {
				if (key == label) return true;
			}
			return false;
		}

		public void addCatchBlock(int handlerId, ExpressionStack catchStack) {
			mCatchStacks.put(handlerId, catchStack);
		}

		public ExpressionStack getCatchBlock(int handlerId) {
			return mCatchStacks.get(handlerId);
		}

		public String getHandlerType(int handlerId) {
			return mHandlerTypes.get(handlerId);
		}

	}
}
