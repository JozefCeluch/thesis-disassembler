package com.thesis.common;

public abstract class CodeElement implements Writable {
	protected static final String TAB = "\t";

	protected CodeElement mParent;

	public CodeElement(CodeElement parent) {
		mParent = parent;
	}

	public void setParent (CodeElement parent) {
		mParent = parent;
	}

	private int countParents() {
		int parent = 0;
		if (mParent != null) {
			parent = mParent.countParents();
			parent += 1;
		}
		return parent;
	}

	protected String getTabs() {
		StringBuilder buf = new StringBuilder();
		int parents = countParents();
		for(int i = 0; i < parents; i++) {
			buf.append(TAB);
		}
		return buf.toString();
	}
}
