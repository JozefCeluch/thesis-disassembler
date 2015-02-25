package com.thesis.block;

import com.thesis.InstructionTranslator;
import com.thesis.LocalVariable;
import com.thesis.common.DataType;
import com.thesis.common.SignatureVisitor;
import com.thesis.common.Util;
import com.thesis.expression.ReturnExpression;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class MethodBlock extends Block {
	MethodNode mMethodNode;
	private String mClassName;
	private int mClassAccess;
	private Map<Integer, LocalVariable> mArguments;

	private List<Object> mAnnotations;
	private String mAccessFlags;
	private DataType mReturnType;
	private String mName;
	private String mMethodArgs;
	private String mExceptions;
	private String mAbstractMethodDeclarationEnd;

	public MethodBlock(MethodNode methodNode, Block parent) {
		mMethodNode = methodNode;
		mParent = parent;
		mArguments = new HashMap<>();
	}

	public void setClassAccess(int classAccess) {
		mClassAccess = classAccess;
	}

	public void setClassName(String className) {
		mClassName = Util.javaObjectName(className);
	}

	public MethodNode getMethodNode() {
		return mMethodNode;
	}

	public String getClassName() {
		return mClassName;
	}

	public Map<Integer, LocalVariable> getArguments() {
		return mArguments;
	}

	public Block disassemble() {
		mAnnotations = getSingleLineAnnotations(mMethodNode.visibleAnnotations, mMethodNode.invisibleAnnotations);
		//TODO parameter annotations, easy with debug info
		appendMethodNode(mMethodNode);
		disassembleCodeBlock();
		return this;
	}

	private void disassembleCodeBlock() {
		clearBuffer();
		if (!Util.containsFlag(mMethodNode.access, Opcodes.ACC_ABSTRACT)){
			InstructionTranslator translator = new InstructionTranslator(this);
			children.addAll(translator.addCode());
		}
	}

	private void appendMethodNode(MethodNode method) {
		clearBuffer();

		StringBuilder genericDecl = new StringBuilder();
		StringBuilder genericReturn = new StringBuilder();
		StringBuilder genericExceptions = new StringBuilder();

		parseSignature(method, genericDecl, genericReturn, genericExceptions);

		mAccessFlags = getAccessFlags(method.access);

		mReturnType = getReturnType(method.desc, genericReturn.toString());
		mName = Util.isConstructor(method.name) ? mClassName : method.name;

		generateArguments(method);

		mMethodArgs = getMethodArgs(method, genericDecl.toString());
		mExceptions = getExceptions(method.exceptions, genericExceptions.toString());
		mAbstractMethodDeclarationEnd = getAbstractMethodDeclarationEnding(method);
	}

	private void parseSignature(MethodNode method, StringBuilder genericDecl, StringBuilder genericReturn, StringBuilder genericExceptions) {
		if (method.signature != null) {
			SignatureVisitor visitor = new SignatureVisitor(0, method.visibleParameterAnnotations, method.invisibleParameterAnnotations);
			visitor.setLocalVariableNodes(method.localVariables);
			visitor.setStatic(Util.containsFlag(method.access, Opcodes.ACC_STATIC));
			SignatureReader signatureReader = new SignatureReader(method.signature);
			signatureReader.accept(visitor);
			if (visitor.getDeclaration() != null) genericDecl.append(visitor.getDeclaration());
			if (visitor.getReturnType() != null) genericReturn.append(visitor.getReturnType());
			if (visitor.getExceptions() != null) genericExceptions.append(visitor.getExceptions());
			mArguments.putAll(visitor.getArguments());
			if (genericDecl.indexOf("<") == 0) {
				int gtPosition = genericDecl.indexOf(">") + 1;
				genericReturn.insert(0, " ");
				genericReturn.insert(0, genericDecl.substring(0, gtPosition));
				genericDecl.replace(0, gtPosition, "");
			}
		}
	}

	private String getAccessFlags(int access) {
		clearBuffer();
		addAccess(access & ~Opcodes.ACC_VOLATILE);

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
		if (Util.containsFlag(access, Opcodes.ACC_TRANSIENT)) {
			removeFromBuffer("transient ");
		}
		return buf.toString();
	}

	private DataType getReturnType(String desc, String genericReturn) {
		if (Util.isNotEmpty(genericReturn)){
			return DataType.getType(genericReturn);
		} else {
			int closingBracketPosition = desc.lastIndexOf(')');
			return Util.getType(desc.substring(closingBracketPosition + 1));
		}
	}

	private String getMethodArgs(MethodNode method, String genericDecl) {
		boolean isStatic = Util.containsFlag(method.access, Opcodes.ACC_STATIC);
		clearBuffer();
		buf.append("(");
		if (Util.isNotEmpty(genericDecl)) {
			buf.append(genericDecl);
		} else {
			int maxArgumentCount;
			if (isStatic){
				maxArgumentCount = mArguments.size();
			} else {
				maxArgumentCount = mArguments.size()-1;
			}
			for (int i = 0; i < maxArgumentCount; i++) {
				addComma(i);
				addAnnotations(method, i);
				LocalVariable variable = mArguments.get(isStatic ? i : i+1);
				buf.append(variable.getType().print()).append(" ").append(variable.toString());
			}
		}
		if (Util.containsFlag(method.access, Opcodes.ACC_VARARGS)) {
			int lastBrackets = buf.lastIndexOf("[]");
			buf.replace(lastBrackets, lastBrackets+2, "...");
		}
		buf.append(")");
		return buf.toString();
	}

	private void generateArguments(MethodNode method) {
		boolean isStatic = Util.containsFlag(mMethodNode.access, Opcodes.ACC_STATIC);
		if (!isStatic) {
			LocalVariable thisArgument = new LocalVariable("this", DataType.getType(mClassName), 0);
			thisArgument.setIsArgument(true);
			mArguments.put(0, thisArgument);
		}

		int closingBracketPosition = method.desc.lastIndexOf(')');
		String args = method.desc.substring(1, closingBracketPosition);
		String[] splitArgs = splitMethodArguments(args);

		for (int i = 0; i < splitArgs.length; i++) {
			if (!splitArgs[i].isEmpty()) {
				addArgument(splitArgs[i], i, method.localVariables, isStatic);
			}
		}
	}

	private void addArgument(String typeCode, int index, List localVariables, boolean isStatic) {
		LocalVariableNode variableNode = Util.variableAtIndex(isStatic ? index : index + 1, localVariables);

		LocalVariable variable;
		if (variableNode == null) {
			DataType type = Util.getType(typeCode);
			String name = Util.ARGUMENT_NAME_BASE + index;
			variable =  new LocalVariable(name, type, isStatic ? index : index + 1);
		} else {
			variable = new LocalVariable(variableNode);
		}
		variable.setIsArgument(true);

		mArguments.put(variable.getIndex(), variable);
	}

	private void addAnnotations(MethodNode method, int i) {
		addParameterAnnotations(method.invisibleParameterAnnotations, i);
		addParameterAnnotations(method.visibleParameterAnnotations, i);
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

	private String getExceptions(List exceptions, String genericExceptions) {
		clearBuffer();
		if (Util.isNotEmpty(genericExceptions)) {
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
		return buf.toString();
	}

	private String[] splitMethodArguments(final String args) {
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

	private String getAbstractMethodDeclarationEnding(MethodNode method) {
		clearBuffer();
		if (Util.containsFlag(method.access, Opcodes.ACC_ABSTRACT)) {
			if (method.annotationDefault != null) {
				buf.append(" default ");
				addAnnotationValue(method.annotationDefault);
			}
			addStatementEnd();
		}
		return buf.toString();
	}

	private void addAnnotationValue(Object value) {
		buf.append(mAnnotationParser.getAnnotationValue(value));
	}

	@Override
	public void write(Writer writer) throws IOException {
		printList(writer, mAnnotations);
		writer.write(mAccessFlags);
		writer.write(Util.isConstructor(mMethodNode.name) ? "" : (mReturnType.print() + " "));
		writer.write(mName);
		writer.write(mMethodArgs);
		writer.write(mExceptions);
		writer.write(mAbstractMethodDeclarationEnd);

		if (!Util.containsFlag(mMethodNode.access, Opcodes.ACC_ABSTRACT)){
			writer.write(BLOCK_START);
			for(Block child : children) {
				if (Util.isConstructor(mMethodNode.name) && child instanceof Statement && ((Statement) child).mExpression instanceof ReturnExpression) continue; // todo constructor return statement
				child.write(writer);
			}
			writer.write(BLOCK_END);
		}
	}

}
