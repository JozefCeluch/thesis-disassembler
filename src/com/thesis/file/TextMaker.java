package com.thesis.file;

import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceSignatureVisitor;

import java.util.ArrayList;
import java.util.List;

public class TextMaker extends Textifier {

    private static final String COMMENT = "// ";
    private static final String NEW_LINE = "\n";
    private static final String LEFT_BRACKET_NL = "{\n";
    private static final String RIGHT_BRACKET_NL = "}\n";
	private static final String TAB = "\t";
	private int accessFlags;
	private int classVersion;
	private String className;
	private boolean isEnum;

	public TextMaker(int api) {
		super(api);
	}

	public TextMaker() {
		this(Opcodes.ASM5);
	}

	private static boolean containsFlag(int value, int flag) {
		return (value & flag) != 0;
	}

    private static String javaObjectName(String objectName) {
        return objectName.replaceAll("/", ".").replaceAll(";","");
    }

	//region classes
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		accessFlags = access;
		className = name;
		classVersion = version;
		int major = version & 0xFFFF;
		int minor = version >>> 16;
		boolean isClass = false;

		clearBuffer();
//        todo print to log file
//        buf.append(COMMENT).append("class version ").append(major).append(".").append(minor).append(NEW_LINE);

		appendDeprecatedAnnotationIfNeeded(access);

		appendAccess(access & ~Opcodes.ACC_SUPER);

		if (containsFlag(access, Opcodes.ACC_ENUM)) isEnum = true; //enum has a weird signature

		if (signature != null && !isEnum) {
			appendFieldSignature(signature);
		} else {
			if (containsFlag(access, Opcodes.ACC_ANNOTATION)) {
				buf.append("@interface ");
				removeFromBuffer("abstract ");
			} else if (containsFlag(access, Opcodes.ACC_INTERFACE)) {
				buf.append("interface "); // interface is implicitly abstract
				removeFromBuffer("abstract ");
			} else if (!containsFlag(access, Opcodes.ACC_ENUM)) {
				buf.append("class ");
				isClass = true;
			} else {
				isEnum = true;
				removeFromBuffer("final ");
			}
		}

		buf.append(name);

		if (!isEnum) { // every enum implicitly extends java.lang.Enum
			appendSuperClass(superName);
		}

		if (isClass || isEnum) {
			appendInterfaces(interfaces);
		}

		appendBlockBeginning();

		text.add(buf.toString());
	}

	@Override
	public void visitSource(String file, String debug) {
//        super.visitSource(file, debug); TODO logging
	}

    @Override
	public void visitOuterClass(String owner, String name, String desc) {
		super.visitOuterClass(owner, name, desc);
	}

    @Override
	public Textifier visitClassAnnotation(String desc, boolean visible) {
//        return super.visitClassAnnotation(desc, visible);
		return this;
	}

    @Override
	public Printer visitClassTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		return visitTypeAnnotation(typeRef, typePath, desc, visible);
	}

    @Override
	public void visitClassAttribute(Attribute attr) {
		super.visitClassAttribute(attr);
	}

    @Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		visit(classVersion, access, name, null, null, new String[0]);
	}

	@Override
	public Textifier visitField(int access, String name, String desc, String signature, Object value) {
		clearBuffer();
		if (!containsFlag(access, Opcodes.ACC_SYNTHETIC)) { //synthetic fields are not generated back to source code
			buf.append(NEW_LINE);
			buf.append(TAB);
			if (appendDeprecatedAnnotationIfNeeded(access)) {
				buf.append(TAB);
			}

			appendAccess(access);

			if (signature != null) {
				appendFieldSignature(signature);
			} else {
				appendType(desc);
			}
			buf.append(name);
			if (value != null) {
				buf.append(" = ").append(value);
			}
			buf.append(";" + NEW_LINE);
		}
		return addBufferToTextAndReturnNewInstance();
	}

	@Override
	public Textifier visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		clearBuffer();

		boolean hasVarargs = false;

		if (!containsFlag(access, Opcodes.ACC_SYNTHETIC) && !containsFlag(access, Opcodes.ACC_BRIDGE)) {
			buf.append(NEW_LINE);
			buf.append(TAB);

			if (appendDeprecatedAnnotationIfNeeded(access)) {
				buf.append(TAB);
			}

			appendAccess(access & ~Opcodes.ACC_VOLATILE);

			if (containsFlag(access, Opcodes.ACC_NATIVE)) {
				buf.append("native ");
			}
			if (containsFlag(access, Opcodes.ACC_VARARGS)) {
				removeFromBuffer("transient ");
				hasVarargs = true;
			}
			if (containsFlag(accessFlags, Opcodes.ACC_INTERFACE) && !containsFlag(access, Opcodes.ACC_ABSTRACT)
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
					genericReturn = genericDecl.substring(0, gtPosition)+ " " + genericReturn;
					genericDecl = genericDecl.substring(gtPosition);
				}
			}

			if (name.equals("<init>")) {
				buf.append(className);
			} else {
				appendMethodReturnType(desc, genericReturn);
				buf.append(name);
			}
			appendMethodArgs(desc, genericDecl, hasVarargs);
			appendExceptions(exceptions, genericExceptions);
			buf.append(" ").append(LEFT_BRACKET_NL);
		}
		return addBufferToTextAndReturnNewInstance();
	}

	@Override
	public void visitClassEnd() {
		super.visitClassEnd();
	}
	//endregion
	//region annotations
	@Override
	public void visit(String name, Object value) {
		super.visit(name, value);
	}

    @Override
	public void visitEnum(String name, String desc, String value) {
		super.visitEnum(name, desc, value);
	}

    @Override
	public Textifier visitAnnotation(String name, String desc) {
		return super.visitAnnotation(name, desc);
	}


    @Override
	public Textifier visitArray(String name) {
		return super.visitArray(name);
	}

    @Override
	public void visitAnnotationEnd() {
		super.visitAnnotationEnd();
	}
	//endregion
	//region fields
	@Override
	public Textifier visitFieldAnnotation(String desc, boolean visible) {
		return visitAnnotation(desc, visible);
	}

    @Override
	public Printer visitFieldTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		return visitTypeAnnotation(typeRef, typePath, desc, visible);
	}

    @Override
	public void visitFieldAttribute(Attribute attr) {
		visitAttribute(attr);
	}

    @Override
	public void visitFieldEnd() {
		super.visitFieldEnd();
	}
	//endregion
	//region methods
	@Override
	public void visitParameter(String name, int access) {
		super.visitParameter(name, access);
	}

    @Override
	public Textifier visitAnnotationDefault() {
		return visitAnnotationDefault();
	}

    @Override
	public Textifier visitMethodAnnotation(String desc, boolean visible) {
		return super.visitMethodAnnotation(desc, visible);
	}

    @Override
	public Printer visitMethodTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		return this.visitTypeAnnotation(typeRef, typePath, desc, visible);
	}

    @Override
	public Textifier visitParameterAnnotation(int parameter, String desc, boolean visible) {
		return super.visitParameterAnnotation(parameter, desc, visible);
	}

    @Override
	public void visitMethodAttribute(Attribute attr) {
		super.visitMethodAttribute(attr);
	}

    @Override
	public void visitCode() {
		super.visitCode();
	}

    @Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		super.visitFrame(type, nLocal, local, nStack, stack);
	}

    @Override
	public void visitInsn(int opcode) {
//        super.visitInsn(opcode); TODO
	}

    @Override
	public void visitIntInsn(int opcode, int operand) {
//        super.visitIntInsn(opcode, operand); TODO
	}

    @Override
	public void visitVarInsn(int opcode, int var) {
//        super.visitVarInsn(opcode, var); TODO
	}

    @Override
	public void visitTypeInsn(int opcode, String type) {
//        super.visitTypeInsn(opcode, type); TODO
	}

    @Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
//        super.visitFieldInsn(opcode, owner, name, desc);  TODO
	}

    @Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
//        super.visitMethodInsn(opcode, owner, name, desc); TODO
	}

    @Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
//        super.visitMethodInsn(opcode, owner, name, desc, itf); TODO
	}

    @Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
//        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs); TODO
	}

    @Override
	public void visitJumpInsn(int opcode, Label label) {
//        super.visitJumpInsn(opcode, label); TODO
	}

    @Override
	public void visitLabel(Label label) {
//        super.visitLabel(label); TODO
	}

    @Override
	public void visitLdcInsn(Object cst) {
//        super.visitLdcInsn(cst); TODO
	}

    @Override
	public void visitIincInsn(int var, int increment) {
//        super.visitIincInsn(var, increment); TODO
	}

    @Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
//        super.visitTableSwitchInsn(min, max, dflt, labels); TODO
	}

    @Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
//        super.visitLookupSwitchInsn(dflt, keys, labels); TODO
	}

    @Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
//        super.visitMultiANewArrayInsn(desc, dims); TODO
	}

    @Override
	public Printer visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
//        return super.visitInsnAnnotation(typeRef, typePath, desc, visible); TODO
		return this;
    }

    @Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
//        super.visitTryCatchBlock(start, end, handler, type); TODO
	}

    @Override
	public Printer visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
//        return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible); TODO
		return this;
    }

    @Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
//        super.visitLocalVariable(name, desc, signature, start, end, index); TODO
	}

    @Override
	public Printer visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
//        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible); TODO
		return this;
	}

    @Override
	public void visitLineNumber(int line, Label start) {
//        super.visitLineNumber(line, start); TODO
	}


    @Override
	public void visitMaxs(int maxStack, int maxLocals) {
//        super.visitMaxs(maxStack, maxLocals); TODO
	}

    @Override
	public void visitMethodEnd() {
		text.add(TAB+RIGHT_BRACKET_NL);
	}
	//endregion
	//region common
	@Override
	public Textifier visitAnnotation(String desc, boolean visible) {
		return super.visitAnnotation(desc, visible);
	}


	@Override
	public Textifier visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
	}

	@Override
	public void visitAttribute(Attribute attr) {
		super.visitAttribute(attr);
	}
	//endregion
	//region utils

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

	private void appendBlockBeginning() {
		buf.append(" ").append(LEFT_BRACKET_NL);
	}

	private void appendInterfaces(String[] interfaces) {
		if (interfaces != null && interfaces.length > 0) {
			buf.append(" implements ");
			for (int i = 0; i < interfaces.length; i++) {
				buf.append(javaObjectName(interfaces[i]));

				if (i < interfaces.length - 1) {
					buf.append(", ");
				}
			}
		}
    }

	private void appendSuperClass(String superName) {
		if (superName != null && !superName.equals("java/lang/Object")) {
			buf.append(" extends ").append(javaObjectName(superName)).append(" ");
		}
	}

	private void removeFromBuffer(String str) {
		int location = buf.indexOf(str);
		if (location > -1)
			buf.replace(location, location + str.length(), "");
	}

	private void appendFieldSignature(String signature) {
		DecompilerSignatureVisitor sv = new DecompilerSignatureVisitor(0);
		SignatureReader r = new SignatureReader(signature);
		r.acceptType(sv);
		buf.append(sv.getDeclaration()).append(" ");
	}

	private void appendType(String desc) {
		if (desc.startsWith("L")) {
			appendReferenceType(desc);
		}else if (desc.startsWith("[")) {
			appendArrayReference(desc);
		} else {
			appendPrimitiveType(desc);
		}
		buf.append(" ");
	}

	private void appendArrayReference(String desc) {
		int dimensions = desc.lastIndexOf('[') + 1;
		String type = desc.substring(dimensions);
		if (type.startsWith("L")) {
			appendReferenceType(type);
		} else {
			appendPrimitiveType(type);
		}
		for (int i = 0; i < dimensions; i++) {
			buf.append("[]");
		}
	}

	private void appendReferenceType(String desc) {
		buf.append(javaObjectName(desc.substring(1)));
	}

	private void appendPrimitiveType(String desc) {
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
		buf.append(type);
	}

	private TextMaker addBufferToTextAndReturnNewInstance() {
		text.add(buf.toString());
		TextMaker tm = createTextMaker();
		text.add(tm.getText());
		return tm;
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
					appendType(splitArgs[i]);
					buf.append("arg").append(i);
					if (i < splitArgs.length - 1) {
						buf.append(", ");
					}
				}
			}
			buf.append(")");
		}
		if (hasVarargs) {
			int lastBrackets = buf.lastIndexOf("[]");
			buf.replace(lastBrackets, lastBrackets+2, "...");
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

	private void appendMethodReturnType(String desc, String genericReturn) {
		if (genericReturn != null){
			buf.append(genericReturn).append(" ");
		} else {
			int closingBracketPosition = desc.lastIndexOf(')');
			appendType(desc.substring(closingBracketPosition + 1));
		}
	}

	private void appendExceptions(String[] exceptions, String genericExceptions) {
		if (genericExceptions != null) {
			buf.append(genericExceptions);
		} else {
			if (exceptions != null && exceptions.length > 0) {
				buf.append(" throws ");
				for (int i = 0; i < exceptions.length; ++i) {
					buf.append(javaObjectName(exceptions[i]));
					if (i < exceptions.length - 1)
						buf.append(' ');
				}
			}
		}
	}

	private TextMaker createTextMaker() {
		return new TextMaker();
	}

	private void clearBuffer() {
		buf.setLength(0);
	}
    //endregion
}
