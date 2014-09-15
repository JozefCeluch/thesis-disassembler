package com.thesis.file;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Disassembler {
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

	private void addAllTypeAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists){
			addAnnotations(annotationNodeList, " ");
		}
	}

	private void appendAllSingleLineAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists){
			clearBuffer();
			addAnnotations(annotationNodeList, NEW_LINE);
			text.add(buf.toString());
		}
	}

	private void addAnnotations(List<AnnotationNode> annotations, String separator) {
		if (annotations == null) return;
		for(AnnotationNode annotation : annotations) {
			addAnnotationNode(annotation.desc, annotation.values);
			buf.append(separator);
		}
	}

	private void addAnnotationNode(String desc, List values) {
		if (desc != null) {
			buf.append("@").append(javaObjectName(getType(desc))); //todo more complicated annotations?
			if (values != null) {
				buf.append("(");
				for (int i = 0; i < values.size(); i +=2) {
					appendComma(i);
					addAnnotationValue((String) values.get(i), values.get(i + 1));
				}
				buf.append(")");
			}
		}
	}

	private void addAnnotationValue(String name, Object value) {
		if (name != null) {
			buf.append(name).append("=");
		}
		if (value instanceof List<?>) {
			buf.append('{');
			for (int i = 0; i < ((List) value).size(); i++) {
				appendComma(i);
				addAnnotationValue(null, ((List) value).get(i));
			}
			buf.append('}');
		} else if(value instanceof String[]){
			buf.append(getType(((String[]) value)[0])).append(".").append(((String[]) value)[1]);
		} else if (value instanceof String) {
			buf.append("\"").append(value).append("\"");
		} else if (value instanceof Character) {
			buf.append("\'").append(value).append("\'");
		} else if (value instanceof Type) {
			buf.append(javaObjectName(((Type) value).getClassName())).append(".class");
		} else if (value instanceof AnnotationNode){
			addAnnotationNode(((AnnotationNode) value).desc, ((AnnotationNode) value).values);
		} else {
			buf.append(value);
		}
	}

	private void appendClassBeginning(int version, int access, String name, String signature, String superName, List interfaces) {
//		int major = version & 0xFFFF;
//		int minor = version >>> 16;
		boolean isClass = false;
		clearBuffer();
		appendAccess(access & ~Opcodes.ACC_SUPER);

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
			appendSuperClass(superName);
			if (isClass) appendInterfaces(interfaces);
		}

		appendBlockBeginning();

		text.add(buf.toString());
	}

	private void appendFields(List<FieldNode> fields) {
		for (FieldNode field : fields) {
			appendAllSingleLineAnnotations(field.visibleAnnotations, field.invisibleAnnotations);
			clearBuffer();
			appendFieldNode(field.access, field.name, field.desc, field.signature, field.value);
			appendStatementEnd();
			text.add(buf.toString());
		}
	}

	private void appendFieldNode(int access, String name, String desc, String signature, Object value) {
//		//synthetic fields are not generated back to source code
		appendDeprecatedAnnotationIfNeeded(access);
		appendAccess(access);
		if (containsFlag(access, Opcodes.ACC_SYNTHETIC)) {
			appendComment("synthetic");
		}
		if (signature != null) {
			appendFieldSignature(signature);
		} else {
			appendType(desc);
		}
		buf.append(name);
		if (value != null) {
			buf.append(" = ").append(value);
		}
	}

	private void appendMethods(List<MethodNode> methods) {

		for (MethodNode method : methods) {

			appendAllSingleLineAnnotations(method.visibleAnnotations, method.invisibleAnnotations);
			clearBuffer();
			//TODO parameter annotations, easy with debug info
			appendMethodNode(method.access, method.name, method.desc, method.signature, method.exceptions);
			if (containsFlag(method.access, Opcodes.ACC_ABSTRACT)) {
				if (method.annotationDefault != null) {
					buf.append(" default ");
					addAnnotationValue(null, method.annotationDefault);
				}
				appendStatementEnd();
			} else {
				appendBlockBeginning();
//			append code
				appendBlockEnd();
			}
			text.add(buf.toString());
		}

	}
//todo split into smaller methods: (access, name, desc, genRet); (desc, genDecl typeAnnotations); (exceptions, genExceptions)
	//todo add param type annotations to signature visitor
	private void appendMethodNode(int access, String name, String desc, String signature, List<String> exceptions) {
		boolean hasVarargs = false;
		appendDeprecatedAnnotationIfNeeded(access);
		appendAccess(access & ~Opcodes.ACC_VOLATILE);

		if (containsFlag(access, Opcodes.ACC_SYNTHETIC)) {
			appendComment("synthetic");
		}
		if (containsFlag(access, Opcodes.ACC_BRIDGE)) {
			appendComment("bridge");
		}
		if (containsFlag(access, Opcodes.ACC_NATIVE)) {
			buf.append("native ");
		}
		if (containsFlag(access, Opcodes.ACC_VARARGS)) {
			removeFromBuffer("transient ");
			hasVarargs = true;
		}
		if (containsFlag(mClassNode.access, Opcodes.ACC_INTERFACE) && !containsFlag(access, Opcodes.ACC_ABSTRACT)
				&& !containsFlag(access, Opcodes.ACC_STATIC)) {
			buf.append("default ");
		}

		String genericDecl = null;
		String genericReturn = null;
		String genericExceptions = null;
		if (signature != null) {
			DecompilerSignatureVisitor v = new DecompilerSignatureVisitor(0);
			SignatureReader r = new SignatureReader(signature);
			r.accept(v);
			genericDecl = v.getDeclaration();
			genericReturn = v.getReturnType();
			genericExceptions = v.getExceptions();

			if (genericDecl.startsWith("<")) {
				int gtPosition = genericDecl.indexOf('>') + 1;
				genericReturn = genericDecl.substring(0, gtPosition) + " " + genericReturn;
				genericDecl = genericDecl.substring(gtPosition);
			}
		}

		if (name.equals("<init>")) {
			buf.append(removeOuterClasses(mClassNode.name));
		} else {
			appendMethodReturnType(desc, genericReturn);
			buf.append(name);
		}
		appendMethodArgs(desc, genericDecl, hasVarargs);
		appendExceptions(exceptions, genericExceptions);
	}

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

	private void appendMethodReturnType(String desc, String genericReturn) {
		if (genericReturn != null){
			buf.append(genericReturn).append(" ");
		} else {
			int closingBracketPosition = desc.lastIndexOf(')');
			appendType(desc.substring(closingBracketPosition + 1));
		}
	}

	private void appendMethodArgs(String desc, String genericDecl, boolean hasVarargs) {
		if (genericDecl != null) {
			buf.append(genericDecl);
		} else {
			buf.append("(");
			int closingBracketPosition = desc.lastIndexOf(')');
			String args = desc.substring(1, closingBracketPosition);
			String[] splitArgs = splitMethodArguments(args);
			for (int i = 0; i < splitArgs.length; i++) {
				if (!splitArgs[i].isEmpty()) {
					appendComma(i);
					appendType(splitArgs[i]);
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

	private void appendExceptions(List<String> exceptions, String genericExceptions) {
		if (genericExceptions != null) {
			buf.append(genericExceptions);
		} else {
			if (exceptions != null && exceptions.size() > 0) {
				buf.append(" throws ");
				for (int i = 0; i < exceptions.size(); ++i) {
					appendComma(i);
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

	private String getTypeIndicator(String args) {
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

	private boolean appendDeprecatedAnnotationIfNeeded(int access) {
		if (containsFlag(access, Opcodes.ACC_DEPRECATED)) {
			buf.append("@Deprecated").append(NEW_LINE);
			return true;
		}
		return false;
	}

	private void appendAccess(int access) {
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

	private void appendFieldSignature(String signature) {
		DecompilerSignatureVisitor sv = new DecompilerSignatureVisitor(0);
		SignatureReader r = new SignatureReader(signature);
		r.acceptType(sv);
		buf.append(sv.getDeclaration()).append(" ");
	}

	private void appendSuperClass(String superName) {
		if (superName != null && !superName.equals("java/lang/Object")) {
			buf.append(" extends ").append(javaObjectName(superName)).append(" ");
		}
	}

	private void appendInterfaces(List<String> interfaces) {
		if (interfaces != null && interfaces.size() > 0) {
			buf.append(" implements ");
			for (int i = 0; i < interfaces.size(); i++) {
				appendComma(i);
				buf.append(javaObjectName(interfaces.get(i)));
			}
		}
	}

	private void appendType(String desc) {
		buf.append(getType(desc)).append(" ");
	}

	private void appendBlockBeginning() {
		buf.append(" ").append(LEFT_BRACKET).append(NEW_LINE);
	}

	private void appendBlockEnd(){
		buf.append(RIGHT_BRACKET).append(NEW_LINE);
	}

	private void appendComment(String comment) {
		buf.append(" /* ").append(comment).append(" */ ");
	}

	private void appendComma(int currentPosition) {
		if (currentPosition > 0)
			buf.append(", ");
	}

	private void appendStatementEnd() {
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

	private static String getType(String desc){
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

	private static String javaObjectName(String objectName) {
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
