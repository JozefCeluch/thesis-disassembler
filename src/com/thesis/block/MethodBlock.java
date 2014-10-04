package com.thesis.block;

import com.thesis.InstructionTranslator;
import com.thesis.common.SignatureVisitor;
import com.thesis.common.Util;
import com.thesis.expression.ArithmeticExpression;
import com.thesis.expression.AssignmentExpression;
import com.thesis.expression.Expression;
import com.thesis.expression.PrimaryExpression;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MethodBlock extends Block {
	MethodNode mMethodNode;
	private String mClassName;
	private int mClassAccess;

	public MethodBlock(MethodNode methodNode, Block parent) {
		mMethodNode = methodNode;
		mParent = parent;
	}

	public void setClassAccess(int classAccess) {
		mClassAccess = classAccess;
	}

	public void setClassName(String className) {
		mClassName = className;
	}

	public Block disassemble() {
		appendAllSingleLineAnnotations(mMethodNode.visibleAnnotations, mMethodNode.invisibleAnnotations);
		//TODO parameter annotations, easy with debug info
		appendMethodNode(mMethodNode);
		disassembleCodeBlock(mMethodNode);
		return this;
	}

	protected void appendAllSingleLineAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists) {
			text.add(mAnnotationParser.getAnnotations(annotationNodeList, NL));
		}
	}

	private void disassembleCodeBlock(MethodNode method) {
		clearBuffer();
		if (!Util.containsFlag(method.access, Opcodes.ACC_ABSTRACT)){
			InstructionTranslator translator = new InstructionTranslator(mMethodNode, children);
			translator.addCode();
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

		if (Util.containsFlag(method.access, Opcodes.ACC_TRANSIENT)) {
			removeFromBuffer("transient ");
			hasVarargs = true;
		}
		addMethodArgs(method, genericDecl.toString(), hasVarargs);
		addExceptions(method.exceptions, genericExceptions.toString());
		addAbstractMethodDeclarationEnding(method);

		text.add(buf.toString());
	}

	private void parseSignature(MethodNode method, StringBuilder genericDecl, StringBuilder genericReturn, StringBuilder genericExceptions) {
		if (method.signature != null) {
			SignatureVisitor v = new SignatureVisitor(0, method.visibleParameterAnnotations, method.invisibleParameterAnnotations);
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

		if (Util.containsFlag(access, Opcodes.ACC_SYNTHETIC)) {
			addComment("synthetic");
		}
		if (Util.containsFlag(access, Opcodes.ACC_BRIDGE)) {
			addComment("bridge");
		}
		if (Util.containsFlag(access, Opcodes.ACC_NATIVE)) {
			buf.append("native ");
		}

		if (Util.containsFlag(mClassAccess, Opcodes.ACC_INTERFACE) && !Util.containsFlag(access, Opcodes.ACC_ABSTRACT)
				&& !Util.containsFlag(access, Opcodes.ACC_STATIC)) {
			buf.append("default ");
		}

		if (name.equals("<init>")) {
			buf.append(Util.removeOuterClasses(mClassName));
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

	private void addAllTypeAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists){
			buf.append(mAnnotationParser.getAnnotations(annotationNodeList, " "));
		}
	}

	private void addExceptions(List exceptions, String genericExceptions) {
		if (genericExceptions != null && !genericExceptions.isEmpty()) {
			buf.append(genericExceptions);
		} else {
			if (exceptions != null && exceptions.size() > 0) {
				buf.append(" throws ");
				for (int i = 0; i < exceptions.size(); ++i) {
					addComma(i);
					buf.append(Util.javaObjectName((String)exceptions.get(i)));
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
		if (Util.containsFlag(method.access, Opcodes.ACC_ABSTRACT)) {
			if (method.annotationDefault != null) {
				buf.append(" default ");
				addAnnotationValue(method.annotationDefault);
			}
			addStatementEnd();
		}
	}

	private void addAnnotationValue(Object value) {
		buf.append(mAnnotationParser.getAnnotationValue(value));
	}

	@Override
	public void write(Writer writer) throws IOException {
		printList(writer, text);
		if (!Util.containsFlag(mMethodNode.access, Opcodes.ACC_ABSTRACT)){
			writer.write(BLOCK_START);
			//todo print children
//			writer.write(buf.toString());
			for(Block child : children) {
				child.write(writer);
			}
			writer.write(BLOCK_END);
		}
	}
}
