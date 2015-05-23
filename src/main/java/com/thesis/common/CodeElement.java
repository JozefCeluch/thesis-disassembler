package com.thesis.common;

/**
 * General representation of a Java source element
 */
public abstract class CodeElement implements Writable {
	protected static final String TAB = "\t";

	/**
	 * Enclosing CodeElement
	 */
	protected CodeElement mParent;

	public CodeElement(CodeElement parent) {
		mParent = parent;
	}

	public void setParent(CodeElement parent) {
		mParent = parent;
	}

	/**
	 * Counts the number of parents
	 * @return 0 if the element has no parents
	 */
	private int countParents() {
		int parent = 0;
		if (mParent != null) {
			parent = mParent.countParents();
			parent += 1;
		}
		return parent;
	}

	/**
	 * Counts the number of tabs depending on the number of parents
	 * @return string of tabs
	 */
	protected String getTabs() {
		StringBuilder buf = new StringBuilder();
		int parents = countParents();
		for(int i = 0; i < parents; i++) {
			buf.append(TAB);
		}
		return buf.toString();
	}
}
