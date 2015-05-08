package com.thesis.block;

import com.thesis.common.Writable;
import com.thesis.translator.InstructionTranslator;
import com.thesis.expression.variable.LocalVariable;
import com.thesis.common.DataType;
import com.thesis.common.SignatureVisitor;
import com.thesis.common.Util;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class MethodBlock extends Block {
	MethodNode mMethodNode;
	private DataType mClassType;
	private int mClassAccess;
	private Map<Integer, LocalVariable> mArguments;

	private List<Object> mAnnotations;
	private String mAccessFlags;
	private String mGenericReturnType;
	private String mName;
	private String mGenericArgs;
	private String mGenericExceptions;

	public MethodBlock(MethodNode methodNode, Block parent) {
		super(parent);
		mMethodNode = methodNode;
		mArguments = new HashMap<>();
	}

	public void setClassAccess(int classAccess) {
		mClassAccess = classAccess;
	}

	public void setClassType(DataType classType) {
		mClassType = classType;
	}

	public MethodNode getMethodNode() {
		return mMethodNode;
	}

	public DataType getClassType() {
		return mClassType;
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
		parseSignature(method);

		mAccessFlags = getAccessFlags(method.access);
		mName = Util.isConstructor(method.name) ? mClassType.print() : method.name;

		generateArguments(method);
	}

	private void parseSignature(MethodNode method) {
		if (method.signature == null) {
			return;
		}
		SignatureVisitor visitor = new SignatureVisitor(0, method.visibleParameterAnnotations, method.invisibleParameterAnnotations);
		visitor.setLocalVariableNodes(method.localVariables);
		visitor.setStatic(Util.containsFlag(method.access, Opcodes.ACC_STATIC));
		SignatureReader signatureReader = new SignatureReader(method.signature);
		signatureReader.accept(visitor);

		mGenericExceptions = visitor.getExceptions();
		mArguments.putAll(visitor.getArguments());

		mGenericReturnType = visitor.getReturnType();

		if (visitor.getDeclaration() != null) {
			mGenericArgs = visitor.getDeclaration();
			if (mGenericArgs.indexOf("<") == 0) {
				int gtPosition = mGenericArgs.indexOf(">") + 1;
				mGenericReturnType = mGenericArgs.substring(0, gtPosition) + " " + mGenericReturnType;
				mGenericArgs = mGenericArgs.substring(gtPosition);
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
			return DataType.getTypeFromObject(genericReturn);
		} else {
			return DataType.getType(Type.getReturnType(desc));
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
			LocalVariable thisArgument = new LocalVariable("this", mClassType, 0);
			thisArgument.setIsArgument(true);
			mArguments.put(0, thisArgument);
		}

		Type methodType = Type.getMethodType(method.desc);
		for (int i = 0; i < methodType.getArgumentTypes().length; i++) {
			addArgument(methodType.getArgumentTypes()[i], i, method.localVariables, isStatic);
		}
	}

	private void addArgument(Type type, int index, List localVariables, boolean isStatic) {
		LocalVariableNode variableNode = Util.variableAtIndex(isStatic ? index : index + 1, localVariables);

		LocalVariable variable;
		if (variableNode == null) {
			variable =  new LocalVariable(Util.ARGUMENT_NAME_BASE + index, DataType.getType(type), isStatic ? index : index + 1);
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
					buf.append(Type.getObjectType((String)exceptions.get(i)).getClassName());
				}
			}
		}
		return buf.toString();
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
		writer.write(NL);
		printList(writer, mAnnotations);
		String tabs = getTabs();
		writer.write(tabs);
		writer.write(mAccessFlags);
		writer.write(Util.isConstructor(mMethodNode.name) ? "" : (getReturnType(mMethodNode.desc, mGenericReturnType).print() + " "));
		writer.write(mName);
		writer.write(getMethodArgs(mMethodNode, mGenericArgs));
		writer.write(getExceptions(mMethodNode.exceptions, mGenericExceptions));
		writer.write(getAbstractMethodDeclarationEnding(mMethodNode));

		if (!Util.containsFlag(mMethodNode.access, Opcodes.ACC_ABSTRACT)){
			writer.write(BLOCK_START);
			for(Writable child : children) {
				child.write(writer);
			}
			writer.append(tabs).write(BLOCK_END);
		}
	}

}
