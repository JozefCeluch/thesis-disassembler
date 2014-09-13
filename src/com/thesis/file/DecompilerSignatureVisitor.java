package com.thesis.file;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

/**
 * A {@link SignatureVisitor} that prints a disassembled view of the signature
 * it visits. Adjusted version of TraceSignatureVisitor
 *
 * @author Eugene Kuleshov
 * @author Eric Bruneton
 */
public class DecompilerSignatureVisitor extends SignatureVisitor {

	private final StringBuffer declaration;

	private boolean isInterface;

	private boolean seenFormalParameter;

	private boolean seenInterfaceBound;

	private boolean seenParameter;

	private boolean seenInterface;

	private StringBuffer returnType;

	private StringBuffer exceptions;

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

	public DecompilerSignatureVisitor(final int access) {
		super(Opcodes.ASM5);
		isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
		this.declaration = new StringBuffer();
	}

	private DecompilerSignatureVisitor(final StringBuffer buf) {
		super(Opcodes.ASM5);
		this.declaration = buf;
	}

	@Override
	public void visitFormalTypeParameter(final String name) {
		declaration.append(seenFormalParameter ? ", " : "<").append(name);
		seenFormalParameter = true;
		seenInterfaceBound = false;
	}

	@Override
	public SignatureVisitor visitClassBound() {
		separator = " extends ";
		startType();
		return this;
	}

	@Override
	public SignatureVisitor visitInterfaceBound() {
		separator = seenInterfaceBound ? ", " : " extends ";
		seenInterfaceBound = true;
		startType();
		return this;
	}

	@Override
	public SignatureVisitor visitSuperclass() {
		endFormals();
		separator = " extends ";
		startType();
		return this;
	}

	@Override
	public SignatureVisitor visitInterface() {
		separator = seenInterface ? ", " : isInterface ? " extends "
				: " implements ";
		seenInterface = true;
		startType();
		return this;
	}

	@Override
	public SignatureVisitor visitParameterType() {
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
	public SignatureVisitor visitReturnType() {
		endFormals();
		if (seenParameter) {
			seenParameter = false;
		} else {
			declaration.append('(');
		}
		declaration.append(')');
		returnType = new StringBuffer();
		return new DecompilerSignatureVisitor(returnType);
	}

	@Override
	public SignatureVisitor visitExceptionType() {
		if (exceptions == null) {
			exceptions = new StringBuffer();
		} else {
			exceptions.append(", ");
		}
		// startType();
		return new DecompilerSignatureVisitor(exceptions);
	}

	@Override
	public void visitBaseType(final char descriptor) {
		switch (descriptor) {
			case 'V':
				declaration.append("void");
				break;
			case 'B':
				declaration.append("byte");
				break;
			case 'J':
				declaration.append("long");
				break;
			case 'Z':
				declaration.append("boolean");
				break;
			case 'I':
				declaration.append("int");
				break;
			case 'S':
				declaration.append("short");
				break;
			case 'C':
				declaration.append("char");
				break;
			case 'F':
				declaration.append("float");
				break;
			// case 'D':
			default:
				declaration.append("double");
				break;
		}
		endType();
	}

	@Override
	public void visitTypeVariable(final String name) {
		declaration.append(name);
		endType();
	}

	@Override
	public SignatureVisitor visitArrayType() {
		startType();
		arrayStack |= 1;
		return this;
	}

	@Override
	public void visitClassType(final String name) {
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
	public SignatureVisitor visitTypeArgument(final char tag) {
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
			declaration.append(" arg").append(argCount);
		}
	}
}
