package com.thesis.block;

import com.thesis.common.CodeElement;
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
public abstract class Block extends CodeElement {
	protected static final String NL = "\n";
	protected static final String OPENING_BRACKET = "{";
	protected static final String CLOSING_BRACKET = "}";
	protected static final String BLOCK_START = " " + OPENING_BRACKET + NL;
	protected static final String BLOCK_END = CLOSING_BRACKET + NL;

	/**
	 * Buffer used for building of Java representation
	 */
	protected StringBuffer buf;

	protected final AnnotationParser mAnnotationParser;

	/**
	 * Children of this block
	 */
	protected List<CodeElement> children;

	/**
	 * @param parent the enclosing block
	 */
	protected Block(Block parent) {
		super(parent);
		buf = new StringBuffer();
		mAnnotationParser = new AnnotationParser();
		children = new ArrayList<>();
	}

	/**
	 * The method that drives the decompilation process
	 * @return instance of this block
	 */
	public abstract Block disassemble();

	protected void clearBuffer() {
		buf.setLength(0);
	}

	/**
	 * Appends the access strings to the StringBuffer
	 * @param access int representing access flags
	 */
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
			buf.append(wrapInComment("synthetic"));
		}
	}

	/**
	 * Appends the string to the buffer enclosed in Java comments
	 * @param comment contents of the comment
	 * @return text in Java comment block
	 */
	protected String wrapInComment(String comment) {
		return " /* " + comment + " */ ";
	}

	/**
	 * Appends semicolon and new line to the buffer
	 */
	protected void addStatementEnd() {
		buf.append(";" + NL);
	}

	/**
	 * Removes the first occurrence of the string from the buffer
	 * @param str text that is to be removed from buffer
	 */
	protected void removeFromBuffer(String str) {
		int location = buf.indexOf(str);
		if (location > -1)
			buf.replace(location, location + str.length(), "");
	}

	/**
	 * Recursively writes the content of the list to the writer
	 * @param pw destination writer
	 * @param l source list
	 */
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

	/**
	 * Converts the annotations in the list to Java source representation
	 * @param annotationLists annotations in class file format
	 * @return list of annotations
	 */
	protected List<Object> getSingleLineAnnotations(List... annotationLists){
		List<Object> annotations = new ArrayList<>();
		String tabs = getTabs();
		for (List annotationNodeList : annotationLists) {
			annotations.add(mAnnotationParser.getAnnotations(annotationNodeList, tabs, NL));
		}
		return annotations;
	}
}
