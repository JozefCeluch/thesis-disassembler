package com.thesis.block;

import com.thesis.common.Writable;
import com.thesis.common.AnnotationParser;
import com.thesis.common.Util;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * General representation of a Java class-level member
 */
public abstract class Block implements Writable{
	protected static final String NL = "\n";
	protected static final String OPENING_BRACKET = "{";
	protected static final String CLOSING_BRACKET = "}";
	protected static final String TAB = "\t";
	protected static final String BLOCK_START = " " + OPENING_BRACKET + NL;
	protected static final String BLOCK_END = CLOSING_BRACKET + NL;

	protected StringBuffer buf;
	protected List<Object> text;
	protected Block mParent;
	protected final AnnotationParser mAnnotationParser;
	protected List<Writable> children;

	protected Block() {
		buf = new StringBuffer();
		text = new ArrayList<>();
		mAnnotationParser = new AnnotationParser();
		children = new ArrayList<>();
	}

	public abstract Block disassemble();

	public List<Writable> getChildren() {
		return children;
	}

	protected void clearBuffer() {
		buf.setLength(0);
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
		if (Util.containsFlag(access, Opcodes.ACC_SYNTHETIC)) {
			addComment("synthetic");
		}
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

	public static void printList(final Writer pw, final List<?> l) {
		for (Object o : l) {
			if (o instanceof List) {
				printList(pw, (List<?>) o);
			} else {
				try {
					pw.write(o.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected List<Object> getSingleLineAnnotations(List... annotationLists){
		List<Object> annotations = new ArrayList<>();
		for (List annotationNodeList : annotationLists) {
			annotations.add(mAnnotationParser.getAnnotations(annotationNodeList, NL));
		}
		return annotations;
	}
}
