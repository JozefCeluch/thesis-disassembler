package com.thesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TryCatchItem {

	private int mStartId;
	private int mEndId;
	private Map<Integer, String> mHandlerLocations = new HashMap<>();

	public TryCatchItem(int startId, int endId, int handlerId, String exception) {
		mStartId = startId;
		mEndId = endId;
		mHandlerLocations.put(handlerId, exception);
	}

	public int getStartId() {
		return mStartId;
	}

	public int getEndId() {
		return mEndId;
	}

	public Map<Integer, String> getHandlerLocations() {
		return mHandlerLocations;
	}

	public void addHandlers(Map<Integer,String> handlers) {
		mHandlerLocations.putAll(handlers);
	}

	public boolean matches(TryCatchItem other) {
		return this.mStartId == other.mStartId && this.mEndId == other.mEndId;
	}
}
