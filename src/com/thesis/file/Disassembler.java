package com.thesis.file;

import org.objectweb.asm.tree.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Disassembler {
	protected final AnnotationParser mAnnotationParser;
	private PrintWriter pw;
	private List<Object> text;

	protected Disassembler() {
		mAnnotationParser = new AnnotationParser();
	}

	public Disassembler(PrintWriter printWriter) {
		this();
		pw = printWriter;
	}

	public void disassembleClass(ClassNode classNode, Block parent) {
		//todo imports
		ClassBlock classBlock = new ClassBlock(classNode, parent);
		text = classBlock.disassemble();
	}

	public void print() {
		printList(pw, text);
	}

	private static void printList(final PrintWriter pw, final List<?> l) {
		for (int i = 0; i < l.size(); ++i) {
			Object o = l.get(i);
			if (o instanceof List) {
				printList(pw, (List<?>) o);
			} else {
				pw.print(o.toString());
			}
		}
	}
}
