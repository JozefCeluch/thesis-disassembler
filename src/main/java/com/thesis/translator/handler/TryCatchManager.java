package com.thesis.translator.handler;

import com.thesis.translator.ExpressionStack;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.util.*;
import java.util.stream.Collectors;

public class TryCatchManager {

	/**
	 * List of try-catch blocks
	 */
	private List<Item> mItems;

	/**
	 * List of try-catch blocks inside the catch blocks that represent the finally statement
	 */
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
				item.addCatchTypes(newItem.getCatchLocations(), newItem.getCatchTypes());
			}
			if (item.getCatchLocations().contains(newItem.getTryStartLocation())) {
				isCatchBlockHandler = true;
			}
			if (foundMatch || isCatchBlockHandler) break;
		}
		if (!foundMatch && !isCatchBlockHandler) {
			tryCatchItems.add(newItem);
		}

		if (isCatchBlockHandler && !newItem.getCatchLocations().contains(newItem.getTryStartLocation())) {
			catchBlockHandlers.add(newItem);
		}
	}

	private void setItems(List<Item> items) {
		mItems = items;
	}

	private void setCatchBlockHandlers(List<Item> catchBlockHandlers) {
		mCatchBlockHandlers = catchBlockHandlers;
	}
	//endregion

	public boolean isEmpty() {
		return mItems == null || mItems.isEmpty();
	}

	public List<Item> getTryBlocksLocation(int labelId) {
		return mItems.stream().filter(item -> item.getTryStartLocation() == labelId).collect(Collectors.toList());
	}

	public boolean hasCatchHandlerEnd(int labelId) {
		for(Item item : mCatchBlockHandlers) {
			if (item.getTryEndLocation() == labelId) return true;
		}
		return false;
	}

	public boolean hasCatchHandlerLocation(int labelId) {
		for(Item item : mCatchBlockHandlers) {
			if (item.getCatchLocations().contains(labelId)) return true;
		}
		return false;
	}

	public boolean hasCatchBlockStart(int location) {
		for (Item item : mItems) {
			if (item.getCatchLocations().contains(location)) {
				return true;
			}
		}
		return false;
	}

	public static class Item {

		private int mTryStartLocation;
		private int mTryEndLocation;
		private Set<Integer> mCatchLocations = new HashSet<>();
		private Map<Integer, ArrayList<String>> mCatchTypes = new HashMap<>(); // location, catch types (multicatch)
		private ExpressionStack mTryStack;
		private Map<Integer, ExpressionStack> mCatchStacks = new HashMap<>(); //location, stack

		public Item(int tryStartLocation, int tryEndLocation, int catchLocation, String exception) {
			mTryStartLocation = tryStartLocation;
			mTryEndLocation = tryEndLocation;
			mCatchLocations.add(catchLocation);
			ArrayList<String> exceptions = new ArrayList<>();
			if (exception != null) {
				exceptions.add(exception);
			}
			mCatchTypes.put(catchLocation, exceptions);
		}

		public int getTryStartLocation() {
			return mTryStartLocation;
		}

		public int getTryEndLocation() {
			return mTryEndLocation;
		}

		public ExpressionStack getTryStack() {
			return mTryStack;
		}

		public void setTryStack(ExpressionStack tryStack) {
			mTryStack = tryStack;
		}

		public Map<Integer, ArrayList<String>> getCatchTypes() {
			return mCatchTypes;
		}

		public List<Integer> getCatchLocations() {
			return new ArrayList<>(mCatchLocations);
		}

		public int getHandlerCount() {
			return mCatchTypes.size();
		}

		public int getCatchBlockCount() {
			return mCatchStacks.size();
		}

		public void addCatchTypes(List<Integer> handlerLocations, Map<Integer, ArrayList<String>> catchTypes) {
			mCatchLocations.addAll(handlerLocations);

			for (Integer key : catchTypes.keySet()) {
				if (mCatchTypes.containsKey(key)) {
					mCatchTypes.get(key).addAll(catchTypes.get(key));
				} else {
					mCatchTypes.put(key, catchTypes.get(key));
				}
			}
		}

		public boolean matches(Item other) {
			return this.mTryStartLocation == other.mTryStartLocation && this.mTryEndLocation == other.mTryEndLocation;
		}

		public boolean hasHandlerLabel(int label) {
			for(int key : mCatchTypes.keySet()) {
				if (key == label) return true;
			}
			return false;
		}

		public void addCatchBlock(int handlerId, ExpressionStack catchStack) {
			mCatchStacks.put(handlerId, catchStack);
		}

		public ExpressionStack getCatchBlock(int catchLocation) {
			return mCatchStacks.get(catchLocation);
		}

		public ArrayList<String> getHandlerType(int catchLocation) {
			return mCatchTypes.get(catchLocation);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Item item = (Item) o;

			if (mTryStartLocation != item.mTryStartLocation) return false;
			if (mTryEndLocation != item.mTryEndLocation) return false;
			if (mCatchLocations != null ? !mCatchLocations.equals(item.mCatchLocations) : item.mCatchLocations != null)
				return false;
			if (mCatchTypes != null ? !mCatchTypes.equals(item.mCatchTypes) : item.mCatchTypes != null) return false;
			if (mTryStack != null ? !mTryStack.equals(item.mTryStack) : item.mTryStack != null) return false;
			return !(mCatchStacks != null ? !mCatchStacks.equals(item.mCatchStacks) : item.mCatchStacks != null);

		}

		@Override
		public int hashCode() {
			int result = mTryStartLocation;
			result = 31 * result + mTryEndLocation;
			result = 31 * result + (mCatchLocations != null ? mCatchLocations.hashCode() : 0);
			result = 31 * result + (mCatchTypes != null ? mCatchTypes.hashCode() : 0);
			result = 31 * result + (mTryStack != null ? mTryStack.hashCode() : 0);
			result = 31 * result + (mCatchStacks != null ? mCatchStacks.hashCode() : 0);
			return result;
		}
	}
}
