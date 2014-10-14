package com.thesis.common;

import com.thesis.LocalVariable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link org.objectweb.asm.signature.SignatureVisitor} that prints a disassembled view of the signature
 * it visits. Adjusted version of TraceSignatureVisitor
 *
 * @author Eugene Kuleshov
 * @author Eric Bruneton
 */
public class SignatureVisitor extends org.objectweb.asm.signature.SignatureVisitor {

	private final StringBuffer declaration;

	private boolean isInterface;

	private boolean seenFormalParameter;

	private boolean seenInterfaceBound;

	private boolean seenParameter;

	private boolean seenInterface;

	private StringBuffer returnType;

	private StringBuffer exceptions;

	private StringBuffer annotations;

	private int argCount;

	/**
	 * Stack used to keep track of class types that have arguments. Each element
	 * of this stack is a boolean encoded in one bit. The top of the stack is
	 * the lowest order bit. Pushing false = *2, pushing true = *2+1, popping =
	 * /2.
	 */
	private int argumentStack;

	/**
	 * Stack used to keep track of array class types. Each element of this stack
	 * is a boolean encoded in one bit. The top of the stack is the lowest order
	 * bit. Pushing false = *2, pushing true = *2+1, popping = /2.
	 */
	private int arrayStack;

	private String separator = "";

	private List[] visibleParamAnnotations;

	private List[] invisibleParamAnnotations;

	private AnnotationParser annotationParser;

	private List mLocalVariableNodes;

	private Map<Integer,LocalVariable> mLocalVariables;

	private LocalVariable currentArgument;
	private int mIndex = 1;

	public SignatureVisitor(final int access, List[] visibleParameterAnnotations, List[] invisibleParameterAnnotations) {
		super(Opcodes.ASM5);
		isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
		this.declaration = new StringBuffer();
		this.visibleParamAnnotations = visibleParameterAnnotations;
		this.invisibleParamAnnotations = invisibleParameterAnnotations;
		annotationParser = new AnnotationParser();
		annotations = new StringBuffer();
		mLocalVariableNodes = null;
		mLocalVariables = new HashMap<>();
	}

	public SignatureVisitor(final int access) {
		super(Opcodes.ASM5);
		isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
		this.declaration = new StringBuffer();
		mLocalVariableNodes = null;
	}

	private SignatureVisitor(final StringBuffer buf) {
		super(Opcodes.ASM5);
		this.declaration = buf;
		mLocalVariableNodes = null;
	}

	public void setLocalVariableNodes(List localVariableNodes) {
		mLocalVariableNodes = localVariableNodes;
	}

	@Override
	public void visitFormalTypeParameter(final String name) {
		declaration.append(seenFormalParameter ? ", " : "<").append(name);
		seenFormalParameter = true;
		seenInterfaceBound = false;
	}

	@Override
	public org.objectweb.asm.signature.SignatureVisitor visitClassBound() {
		separator = " extends ";
		startType();
		return this;
	}

	@Override
	public org.objectweb.asm.signature.SignatureVisitor visitInterfaceBound() {
		separator = seenInterfaceBound ? ", " : " extends ";
		seenInterfaceBound = true;
		startType();
		return this;
	}

	@Override
	public org.objectweb.asm.signature.SignatureVisitor visitSuperclass() {
		endFormals();
		separator = " extends ";
		startType();
		return this;
	}

	@Override
	public org.objectweb.asm.signature.SignatureVisitor visitInterface() {
		separator = seenInterface ? ", " : isInterface ? " extends "
				: " implements ";
		seenInterface = true;
		startType();
		return this;
	}

	@Override
	public org.objectweb.asm.signature.SignatureVisitor visitParameterType() {
		endFormals();
		if (seenParameter) {
			declaration.append(", ");
			argCount++;
		} else {
			seenParameter = true;
			declaration.append('(');
		}
		startType();
		return this;
	}

	@Override
	public org.objectweb.asm.signature.SignatureVisitor visitReturnType() {
		endFormals();
		if (seenParameter) {
			seenParameter = false;
		} else {
			declaration.append('(');
		}
		declaration.append(')');
		returnType = new StringBuffer();
		return new SignatureVisitor(returnType);
	}

	@Override
	public org.objectweb.asm.signature.SignatureVisitor visitExceptionType() {
		if (exceptions == null) {
			exceptions = new StringBuffer();
		} else {
			exceptions.append(", ");
		}
		// startType();
		return new SignatureVisitor(exceptions);
	}

	@Override
	public void visitBaseType(final char descriptor) {
		declaration.append(getCurrentArgAnnotations());
		String type;
		switch (descriptor) {
			case 'V':
				type = "void";
				break;
			case 'B':
				type = "byte";
				break;
			case 'J':
				type = "long";
				break;
			case 'Z':
				type = "boolean";
				break;
			case 'I':
				type = "int";
				break;
			case 'S':
				type = "short";
				break;
			case 'C':
				type = "char";
				break;
			case 'F':
				type = "float";
				break;
			// case 'D':
			default:
				type = "double";
				break;
		}
		declaration.append(type);
		currentArgument.setType(type);
		endType();
	}

	private String getCurrentArgAnnotations() {
		if (argumentStack % 2 != 0 || arrayStack % 2 != 0) return "";

		createVariable();
		if (annotations == null) return "";

		annotations.setLength(0);
		if (visibleParamAnnotations != null) {
			if(visibleParamAnnotations[argCount] != null) {
				annotations.append(annotationParser.getAnnotations(visibleParamAnnotations[argCount], " "));
			}
		}

		if (invisibleParamAnnotations != null) {
			if(invisibleParamAnnotations[argCount] != null) {
				annotations.append(annotationParser.getAnnotations(invisibleParamAnnotations[argCount], " "));
			}
		}

		return annotations.toString();
	}

	@Override
	public void visitTypeVariable(final String name) {
		declaration.append(getCurrentArgAnnotations());
		declaration.append(name);
		currentArgument.setType(name);
		endType();
	}

	@Override
	public org.objectweb.asm.signature.SignatureVisitor visitArrayType() {
		declaration.append(getCurrentArgAnnotations());
		startType();
		arrayStack |= 1;
		return this;
	}

	@Override
	public void visitClassType(final String name) {
		declaration.append(getCurrentArgAnnotations());
		if ("java/lang/Object".equals(name)) {
			// Map<java.lang.Object,java.util.List>
			// or
			// abstract public V get(Object key); (seen in Dictionary.class)
			// should have Object
			// but java.lang.String extends java.lang.Object is unnecessary
			boolean needObjectClass = argumentStack % 2 != 0 || seenParameter;
			if (needObjectClass) {
				declaration.append(separator).append(name.replace('/', '.'));
			}
		} else {
			declaration.append(separator).append(name.replace('/', '.'));
		}
		separator = "";
		argumentStack *= 2;
	}

	@Override
	public void visitInnerClassType(final String name) {
		declaration.append(getCurrentArgAnnotations());
		if (argumentStack % 2 != 0) {
			declaration.append('>');
		}
		argumentStack /= 2;
		declaration.append('.');
		declaration.append(separator).append(name.replace('/', '.'));
		separator = "";
		argumentStack *= 2;
	}

	@Override
	public void visitTypeArgument() {
		if (argumentStack % 2 == 0) {
			++argumentStack;
			declaration.append('<');
		} else {
			declaration.append(", ");
		}
		declaration.append('?');
	}

	@Override
	public org.objectweb.asm.signature.SignatureVisitor visitTypeArgument(final char tag) {
		if (argumentStack % 2 == 0) {
			++argumentStack;
			declaration.append('<');
		} else {
			declaration.append(", ");
		}

		if (tag == EXTENDS) {
			declaration.append("? extends ");
		} else if (tag == SUPER) {
			declaration.append("? super ");
		}

		startType();
		return this;
	}

	@Override
	public void visitEnd() {
		if (argumentStack % 2 != 0) {
			declaration.append('>');
		}
		argumentStack /= 2;
		endType();
	}

	public String getDeclaration() {
		return declaration.toString();
	}

	public String getReturnType() {
		return returnType == null ? null : returnType.toString();
	}

	public String getExceptions() {
		return exceptions == null ? null : exceptions.toString();
	}

	public Map<Integer,LocalVariable>  getArguments() {
		return mLocalVariables;
	}

	// -----------------------------------------------

	private void endFormals() {
		if (seenFormalParameter) {
			declaration.append('>');
			seenFormalParameter = false;
		}
	}

	private void startType() {
		arrayStack *= 2;
	}

	private void endType() {
		if (arrayStack % 2 == 0) {
			arrayStack /= 2;
		} else {
			while (arrayStack % 2 != 0) {
				arrayStack /= 2;
				declaration.append("[]");
			}
		}
		if (seenParameter && argumentStack == 0){
			currentArgument.setIndex(mIndex);
			String name = getArgumentName(mIndex);
			currentArgument.setName(name);
			mLocalVariables.put(currentArgument.getIndex(), currentArgument);
			declaration.append(" ").append(name);
			if (currentArgument.getType()!= null && (currentArgument.getType().equals("double") || currentArgument.getType().equals("long"))) {
				mIndex += 2;
			} else {
				mIndex += 1;
			}
		}
	}

	private String getArgumentName(int index) {
		String name;
		if (mLocalVariableNodes != null && !mLocalVariableNodes.isEmpty()) {
			LocalVariableNode node = Util.variableAtIndex(index, mLocalVariableNodes);
			name = node.name;
		} else {
			name = Util.ARGUMENT_NAME_BASE + argCount;
		}
		return name;
	}

	private void createVariable() {
		currentArgument = new LocalVariable(argCount);
		currentArgument.setIsArgument(true);
	}
}
