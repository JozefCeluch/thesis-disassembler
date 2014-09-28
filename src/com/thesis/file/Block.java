package com.thesis.file;

import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public abstract class Block extends Disassembler{
	protected static final String NL = "\n";
	protected static final String LEFT_BRACKET = "{";
	protected static final String RIGHT_BRACKET = "}";
	protected static final String TAB = "\t";
	protected static final String BLOCK_START = " " + LEFT_BRACKET + NL;
	protected static final String BLOCK_END = RIGHT_BRACKET + NL;

	protected StringBuffer buf;
	protected List<Object> text;
	protected Block mParent;

	protected Block() {
		buf = new StringBuffer();
		text = new ArrayList<>();
	}

	protected static String getTypeIndicator(String args) {
		if (args.startsWith("L")) {
			int positionAfterSemicolon = args.indexOf(';') + 1;
			return args.substring(0, positionAfterSemicolon);
		} else {
			return args.substring(0, 1);
		}
	}

	protected void clearBuffer() {
		buf.setLength(0);
	}

	protected boolean addDeprecatedAnnotationIfNeeded(int access) {
		if (Util.containsFlag(access, Opcodes.ACC_DEPRECATED)) {
			buf.append("@Deprecated").append(NL);
			return true;
		}
		return false;
	}

	protected void addAccess(int access) {
		if (Util.containsFlag(access, Opcodes.ACC_PRIVATE)) {
			buf.append("private ");
		}
		if (Util.containsFlag(access, Opcodes.ACC_PUBLIC)) {
			buf.append("public ");
		}
		if (Util.containsFlag(access, Opcodes.ACC_PROTECTED)) {
			buf.append("protected ");
		}
		if (Util.containsFlag(access, Opcodes.ACC_FINAL)) {
			buf.append("final ");
		}
		if (Util.containsFlag(access, Opcodes.ACC_STATIC)) {
			buf.append("static ");
		}
		if (Util.containsFlag(access, Opcodes.ACC_SYNCHRONIZED)) {
			buf.append("synchronized ");
		}
		if (Util.containsFlag(access, Opcodes.ACC_VOLATILE)) {
			buf.append("volatile ");
		}
		if (Util.containsFlag(access, Opcodes.ACC_TRANSIENT)) {
			buf.append("transient ");
		}
		if (Util.containsFlag(access, Opcodes.ACC_ABSTRACT)) {
			buf.append("abstract ");
		}
		if (Util.containsFlag(access, Opcodes.ACC_STRICT)) {
			buf.append("strictfp ");
		}
		if (Util.containsFlag(access, Opcodes.ACC_ENUM)) {
			buf.append("enum ");
		}
	}

	protected void addType(String desc) {
		buf.append(Util.getType(desc)).append(" ");
	}

	protected void addComment(String comment) {
		buf.append(" /* ").append(comment).append(" */ ");
	}

	protected void addComma(int currentPosition) {
		if (currentPosition > 0)
			buf.append(", ");
	}

	protected void addStatementEnd() {
		buf.append(";" + NL);
	}

	protected void removeFromBuffer(String str) {
		int location = buf.indexOf(str);
		if (location > -1)
			buf.replace(location, location + str.length(), "");
	}

	private void addBlockBeginning() {
		buf.append(" ").append(LEFT_BRACKET).append(NL);
	}

	private void addBlockEnd(){
		buf.append(RIGHT_BRACKET).append(NL);
	}

	public boolean hasParent() {
		return mParent != null;
	}

	public Block getParent() {
		return mParent;
	}

	public int countParents() {
		int parent = getParent().countParents();
		return 1 + parent;
	}
}
