package com.thesis.file;

import jdk.internal.org.objectweb.asm.util.Printer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Disassembler {
	private final AnnotationParser mAnnotationParser;
	private ClassNode mClassNode;
	private StringBuffer buf;
	private List<Object> text;
	private PrintWriter pw;

	private static final String NEW_LINE = "\n";
	private static final String LEFT_BRACKET = "{";
	private static final String RIGHT_BRACKET = "}";
	private static final String TAB = "\t";



	public Disassembler(PrintWriter printWriter) {
		pw = printWriter;
		text = new ArrayList<>();
		buf = new StringBuffer();
		mAnnotationParser = new AnnotationParser();
	}

	public void disassembleClass(ClassNode classNode) {
		mClassNode = classNode;
		//todo imports
		appendAllSingleLineAnnotations(classNode.visibleAnnotations, classNode.invisibleAnnotations);
		appendClassBeginning(classNode.version, classNode.access, classNode.name, classNode.signature, classNode.superName, classNode.interfaces);
		appendFields(classNode.fields);
		appendMethods(classNode.methods);
		appendInnerClasses(classNode.innerClasses);


		appendClassEnd();
	}

	//region annotations
	private void addAllTypeAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists){
			buf.append(mAnnotationParser.getAnnotations(annotationNodeList, " "));
		}
	}

	private void appendAllSingleLineAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists) {
			text.add(mAnnotationParser.getAnnotations(annotationNodeList, NEW_LINE));
		}
	}

	private void addAnnotationValue(Object value) {
		buf.append(mAnnotationParser.getAnnotationValue(value));
	}
	//endregion

	private void appendClassBeginning(int version, int access, String name, String signature, String superName, List interfaces) {
//		int major = version & 0xFFFF;
//		int minor = version >>> 16;
		boolean isClass = false;
		clearBuffer();
		addAccess(access & ~Opcodes.ACC_SUPER);

		if (containsFlag(access, Opcodes.ACC_ANNOTATION)) {
			buf.append("@interface ");
			removeFromBuffer("abstract ");
		} else if (containsFlag(access, Opcodes.ACC_INTERFACE)) {
			buf.append("interface "); // interface is implicitly abstract
			removeFromBuffer("abstract ");
		} else if (!containsFlag(access, Opcodes.ACC_ENUM)) {
			buf.append("class ");
			isClass = true;
		}

		buf.append(removeOuterClasses(name));
		String genericDeclaration = null;
		if (signature != null) {
			DecompilerSignatureVisitor sv = new DecompilerSignatureVisitor(0);
			SignatureReader r = new SignatureReader(signature);
			r.accept(sv);
			genericDeclaration = sv.getDeclaration();
		}

		if (genericDeclaration != null){
			buf.append(genericDeclaration);
		} else {
			addSuperClass(superName);
			if (isClass) addInterfaces(interfaces);
		}

		addBlockBeginning();

		text.add(buf.toString());
	}

	//region fields
	private void appendFields(List<FieldNode> fields) {
		for (FieldNode field : fields) {
			appendAllSingleLineAnnotations(field.visibleAnnotations, field.invisibleAnnotations);
			appendFieldNode(field.access, field.name, field.desc, field.signature, field.value);
		}
	}

	private void appendFieldNode(int access, String name, String desc, String signature, Object value) {
		clearBuffer();
		addDeprecatedAnnotationIfNeeded(access);
		addAccess(access);
		if (containsFlag(access, Opcodes.ACC_SYNTHETIC)) {
			addComment("synthetic");
		}
		if (signature != null) {
			addFieldSignature(signature);
		} else {
			addType(desc);
		}
		buf.append(name);
		if (value != null) {
			buf.append(" = ").append(value);
		}
		addStatementEnd();
		text.add(buf.toString());
	}
	//endregion

	//region methods
	private void appendMethods(List<MethodNode> methods) {
		for (MethodNode method : methods) {
			appendAllSingleLineAnnotations(method.visibleAnnotations, method.invisibleAnnotations);
			//TODO parameter annotations, easy with debug info
			appendMethodNode(method);
			appendCodeBlock(method);
		}

	}

	private void appendMethodNode(MethodNode method) {
		clearBuffer();
		boolean hasVarargs = false;
		StringBuilder genericDecl = new StringBuilder();
		StringBuilder genericReturn = new StringBuilder();
		StringBuilder genericExceptions = new StringBuilder();

		parseSignature(method, genericDecl, genericReturn, genericExceptions);

		addMethodAccessAndName(method.access, method.name, method.desc, genericReturn.toString());

		if (containsFlag(method.access, Opcodes.ACC_TRANSIENT)) {
			removeFromBuffer("transient ");
			hasVarargs = true;
		}
		addMethodArgs(method, genericDecl.toString(), hasVarargs);
		addExceptions(method.exceptions, genericExceptions.toString());
		addAbstractMethodDeclarationEnding(method);

		text.add(buf.toString());
	}

	private void appendCodeBlock(MethodNode method) {
		clearBuffer();
		if (!containsFlag(method.access, Opcodes.ACC_ABSTRACT)){
			addBlockBeginning();
//			todo append code
			addCode(method);
			addBlockEnd();
		}
		text.add(buf.toString());
	}

	//TODO
	private void addCode(MethodNode method) {
		InsnList instructions = method.instructions;
		AbstractInsnNode node = instructions.getFirst();
		while (node != null) {

			switch (node.getType()) {
				case AbstractInsnNode.LINE:
					buf.append(((LineNumberNode) node).line);
					break;
				case AbstractInsnNode.LABEL:
					buf.append(((LabelNode) node).getLabel().info);
					break;
				default:
					buf.append(node).append(": ");
					int opCode = node.getOpcode();
					if (opCode > -1) {
						buf.append(Printer.OPCODES[opCode]);
					}
			}
			buf.append(NEW_LINE);
			node = node.getNext();
		}
	}

	private void parseSignature(MethodNode method, StringBuilder genericDecl, StringBuilder genericReturn, StringBuilder genericExceptions) {
		if (method.signature != null) {
			DecompilerSignatureVisitor v = new DecompilerSignatureVisitor(0, method.visibleParameterAnnotations, method.invisibleParameterAnnotations);
			SignatureReader r = new SignatureReader(method.signature);
			r.accept(v);
			if (v.getDeclaration() != null) genericDecl.append(v.getDeclaration());
			if (v.getReturnType() != null) genericReturn.append(v.getReturnType());
			if (v.getExceptions() != null) genericExceptions.append(v.getExceptions());

			if (genericDecl.indexOf("<") == 0) {
				int gtPosition = genericDecl.indexOf(">") + 1;
				genericReturn.insert(0, " ");
				genericReturn.insert(0, genericDecl.substring(0, gtPosition));
				genericDecl.replace(0, gtPosition, "");
			}
		}
	}

	private void addMethodAccessAndName(int access, String name, String desc, String genericReturn) {
		addDeprecatedAnnotationIfNeeded(access);
		addAccess(access & ~Opcodes.ACC_VOLATILE);

		if (containsFlag(access, Opcodes.ACC_SYNTHETIC)) {
			addComment("synthetic");
		}
		if (containsFlag(access, Opcodes.ACC_BRIDGE)) {
			addComment("bridge");
		}
		if (containsFlag(access, Opcodes.ACC_NATIVE)) {
			buf.append("native ");
		}

		if (containsFlag(mClassNode.access, Opcodes.ACC_INTERFACE) && !containsFlag(access, Opcodes.ACC_ABSTRACT)
				&& !containsFlag(access, Opcodes.ACC_STATIC)) {
			buf.append("default ");
		}

		if (name.equals("<init>")) {
			buf.append(removeOuterClasses(mClassNode.name));
		} else {
			addMethodReturnType(desc, genericReturn);
			buf.append(name);
		}
	}

	private void addMethodReturnType(String desc, String genericReturn) {
		if (genericReturn != null && !genericReturn.isEmpty()){
			buf.append(genericReturn).append(" ");
		} else {
			int closingBracketPosition = desc.lastIndexOf(')');
			addType(desc.substring(closingBracketPosition + 1));
		}
	}

	private void addMethodArgs(MethodNode method, String genericDecl, boolean hasVarargs) {
		if (genericDecl != null && !genericDecl.isEmpty()) {
			buf.append(genericDecl);
		} else {
			buf.append("(");
			int closingBracketPosition = method.desc.lastIndexOf(')');
			String args = method.desc.substring(1, closingBracketPosition);
			String[] splitArgs = splitMethodArguments(args);
			for (int i = 0; i < splitArgs.length; i++) {
				if (!splitArgs[i].isEmpty()) {
					addComma(i);
					addParameterAnnotations(method.invisibleParameterAnnotations, i);
					addParameterAnnotations(method.visibleParameterAnnotations, i);
					addType(splitArgs[i]);
					buf.append("arg").append(i);
				}
			}
			buf.append(")");
		}
		if (hasVarargs) {
			int lastBrackets = buf.lastIndexOf("[]");
			buf.replace(lastBrackets, lastBrackets+2, "...");
		}
	}

	private void addParameterAnnotations(List[] parameterAnnotationsList, int currentParameter) {
		if (parameterAnnotationsList == null) return;
		if (parameterAnnotationsList[currentParameter] != null)
			addAllTypeAnnotations(parameterAnnotationsList[currentParameter]);

	}

	private void addExceptions(List<String> exceptions, String genericExceptions) {
		if (genericExceptions != null && !genericExceptions.isEmpty()) {
			buf.append(genericExceptions);
		} else {
			if (exceptions != null && exceptions.size() > 0) {
				buf.append(" throws ");
				for (int i = 0; i < exceptions.size(); ++i) {
					addComma(i);
					buf.append(javaObjectName(exceptions.get(i)));
				}
			}
		}
	}

	private String[] splitMethodArguments(final String args){
		if (args.isEmpty()) {
			return new String[0];
		}
		List<String> argumentList = new ArrayList<>();
		for (int i = 0; i < args.length();) {
			String brackets = "";
			int bracketEnd = i;

			if (args.charAt(i) == '[') {
				while (args.charAt(bracketEnd) == '['){
					bracketEnd++;
				}
				brackets = args.substring(i, bracketEnd);
			}

			String arg = brackets + getTypeIndicator(args.substring(bracketEnd));
			argumentList.add(arg);
			i += arg.length();
		}

		return argumentList.toArray(new String[argumentList.size()]);
	}

	private void addAbstractMethodDeclarationEnding(MethodNode method) {
		if (containsFlag(method.access, Opcodes.ACC_ABSTRACT)) {
			if (method.annotationDefault != null) {
				buf.append(" default ");
				addAnnotationValue(method.annotationDefault);
			}
			addStatementEnd();
		}
	}
	//endregion

	private void appendInnerClasses(List<InnerClassNode> innerClasses) {
		for (InnerClassNode innerClass : innerClasses) {
			if (mClassNode.name.equals(innerClass.outerName))
				appendInnerClassNode(innerClass.name);
		}
	}

	private void appendInnerClassNode(String name) {
		Parser p = new Parser("testData/"); //todo folder
		try {
			text.add(p.parseClassFile(name + ".class")); //todo make extension optional
			text.add(NEW_LINE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


	private static String getTypeIndicator(String args) {
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

	private void clearBuffer() {
		buf.setLength(0);
	}

	private boolean addDeprecatedAnnotationIfNeeded(int access) {
		if (containsFlag(access, Opcodes.ACC_DEPRECATED)) {
			buf.append("@Deprecated").append(NEW_LINE);
			return true;
		}
		return false;
	}

	private void addAccess(int access) {
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

	private void addFieldSignature(String signature) {
		DecompilerSignatureVisitor sv = new DecompilerSignatureVisitor(0);
		SignatureReader r = new SignatureReader(signature);
		r.acceptType(sv);
		buf.append(sv.getDeclaration()).append(" ");
	}

	private void addSuperClass(String superName) {
		if (superName != null && !superName.equals("java/lang/Object")) {
			buf.append(" extends ").append(javaObjectName(superName)).append(" ");
		}
	}

	private void addInterfaces(List<String> interfaces) {
		if (interfaces != null && interfaces.size() > 0) {
			buf.append(" implements ");
			for (int i = 0; i < interfaces.size(); i++) {
				addComma(i);
				buf.append(javaObjectName(interfaces.get(i)));
			}
		}
	}

	private void addType(String desc) {
		buf.append(getType(desc)).append(" ");
	}

	private void addBlockBeginning() {
		buf.append(" ").append(LEFT_BRACKET).append(NEW_LINE);
	}

	private void addBlockEnd(){
		buf.append(RIGHT_BRACKET).append(NEW_LINE);
	}

	private void addComment(String comment) {
		buf.append(" /* ").append(comment).append(" */ ");
	}

	private void addComma(int currentPosition) {
		if (currentPosition > 0)
			buf.append(", ");
	}

	private void addStatementEnd() {
		buf.append(";" + NEW_LINE);
	}

	private void appendClassEnd() {
		text.add(RIGHT_BRACKET);
	}

	private void removeFromBuffer(String str) {
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

	private static String removeOuterClasses(String name) {
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

	private static boolean containsFlag(int value, int flag) {
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
