package com.thesis.file;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Disassembler {
	private final AnnotationParser mAnnotationParser;
	private ClassNode mClassNode;
	protected StringBuffer buf;
	protected List<Object> text;
	private PrintWriter pw;

	protected static final String NEW_LINE = "\n";
	protected static final String LEFT_BRACKET = "{";
	protected static final String RIGHT_BRACKET = "}";
	protected static final String TAB = "\t";


	protected Disassembler() {
		text = new ArrayList<>();
		buf = new StringBuffer();
		mAnnotationParser = new AnnotationParser();
	}

	public Disassembler(PrintWriter printWriter) {
		this();
		pw = printWriter;
	}

	public void disassembleClass(ClassNode classNode) {
		mClassNode = classNode;
		//todo imports
		ClassBlock classBlock = new ClassBlock(classNode);
		text.add(classBlock.disassemble(classNode));

	}

	//region annotations
	protected void addAllTypeAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists){
			buf.append(mAnnotationParser.getAnnotations(annotationNodeList, " "));
		}
	}

	protected void appendAllSingleLineAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists) {
			text.add(mAnnotationParser.getAnnotations(annotationNodeList, NEW_LINE));
		}
	}

	protected void addAnnotationValue(Object value) {
		buf.append(mAnnotationParser.getAnnotationValue(value));
	}
	//endregion

	protected static String getTypeIndicator(String args) {
		if (args.startsWith("L")) {
			int positionAfterSemicolon = args.indexOf(';') + 1;
			return args.substring(0, positionAfterSemicolon);
		} else {
			return args.substring(0, 1);
		}
	}

	public void print() {
		printList(pw, text);
	}

	protected void clearBuffer() {
		buf.setLength(0);
	}

	protected boolean addDeprecatedAnnotationIfNeeded(int access) {
		if (containsFlag(access, Opcodes.ACC_DEPRECATED)) {
			buf.append("@Deprecated").append(NEW_LINE);
			return true;
		}
		return false;
	}

	protected void addAccess(int access) {
		if (containsFlag(access, Opcodes.ACC_PRIVATE)) {
			buf.append("private ");
		}
		if (containsFlag(access, Opcodes.ACC_PUBLIC)) {
			buf.append("public ");
		}
		if (containsFlag(access, Opcodes.ACC_PROTECTED)) {
			buf.append("protected ");
		}
		if (containsFlag(access, Opcodes.ACC_FINAL)) {
			buf.append("final ");
		}
		if (containsFlag(access, Opcodes.ACC_STATIC)) {
			buf.append("static ");
		}
		if (containsFlag(access, Opcodes.ACC_SYNCHRONIZED)) {
			buf.append("synchronized ");
		}
		if (containsFlag(access, Opcodes.ACC_VOLATILE)) {
			buf.append("volatile ");
		}
		if (containsFlag(access, Opcodes.ACC_TRANSIENT)) {
			buf.append("transient ");
		}
		if (containsFlag(access, Opcodes.ACC_ABSTRACT)) {
			buf.append("abstract ");
		}
		if (containsFlag(access, Opcodes.ACC_STRICT)) {
			buf.append("strictfp ");
		}
		if (containsFlag(access, Opcodes.ACC_ENUM)) {
			buf.append("enum ");
		}
	}

	protected void addType(String desc) {
		buf.append(getType(desc)).append(" ");
	}

	private void addBlockBeginning() {
		buf.append(" ").append(LEFT_BRACKET).append(NEW_LINE);
	}

	private void addBlockEnd(){
		buf.append(RIGHT_BRACKET).append(NEW_LINE);
	}

	protected void addComment(String comment) {
		buf.append(" /* ").append(comment).append(" */ ");
	}

	protected void addComma(int currentPosition) {
		if (currentPosition > 0)
			buf.append(", ");
	}

	protected void addStatementEnd() {
		buf.append(";" + NEW_LINE);
	}

	private void appendClassEnd() {
		text.add(RIGHT_BRACKET);
	}

	protected void removeFromBuffer(String str) {
		int location = buf.indexOf(str);
		if (location > -1)
			buf.replace(location, location + str.length(), "");
	}

	public static String getType(String desc){
		String type;
		if (desc.startsWith("L")) {
			type = getReferenceType(desc);
		}else if (desc.startsWith("[")) {
			type = getArrayReferenceType(desc);
		} else {
			type = getPrimitiveType(desc);
		}
		return removeOuterClasses(type);
	}

	protected static String removeOuterClasses(String name) {
		if (name.contains("$")) {
			int lastName = name.lastIndexOf("$");
			return name.substring(lastName + 1);
		}
		return name;
	}

	private static String getArrayReferenceType(String desc) {
		int dimensions = desc.lastIndexOf('[') + 1;
		String type = desc.substring(dimensions);
		String result = getType(type);
		for (int i = 0; i < dimensions; i++) {
			result += "[]";
		}
		return result;
	}

	private static String getReferenceType(String desc) {
		return javaObjectName(desc.substring(1));
	}

	private static String getPrimitiveType(String desc) {
		String type;
		switch (desc) {
			case "B":
				type = "byte";
				break;
			case "C":
				type = "char";
				break;
			case "D":
				type = "double";
				break;
			case "F":
				type = "float";
				break;
			case "I":
				type = "int";
				break;
			case "J":
				type = "long";
				break;
			case "S":
				type = "short";
				break;
			case "V":
				type = "void";
				break;
			case "Z":
				type = "boolean";
				break;
			default:
				System.out.println("type: " + desc);
				throw new IllegalArgumentException("Unknown primitive type");
		}
		return type;
	}

	public static String javaObjectName(String objectName) {
		return objectName.replaceAll("/", ".").replaceAll(";","");
	}

	protected static boolean containsFlag(int value, int flag) {
		return (value & flag) != 0;
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
