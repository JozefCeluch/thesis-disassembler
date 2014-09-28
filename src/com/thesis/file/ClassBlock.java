package com.thesis.file;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class ClassBlock extends Block {
	ClassNode mClassNode;

	public ClassBlock(ClassNode classNode) {
		mClassNode = classNode;
	}

	public List<Object> disassemble(ClassNode classNode) {

		appendAllSingleLineAnnotations(classNode.visibleAnnotations, classNode.invisibleAnnotations); //todo

		boolean isClass = false;
		clearBuffer();
		addAccess(classNode.access & ~Opcodes.ACC_SUPER);

		if (containsFlag(classNode.access, Opcodes.ACC_ANNOTATION)) {
			buf.append("@interface ");
			removeFromBuffer("abstract ");
		} else if (containsFlag(classNode.access, Opcodes.ACC_INTERFACE)) {
			buf.append("interface "); // interface is implicitly abstract
			removeFromBuffer("abstract ");
		} else if (!containsFlag(classNode.access, Opcodes.ACC_ENUM)) {
			buf.append("class ");
			isClass = true;
		}

		buf.append(removeOuterClasses(classNode.name));
		String genericDeclaration = null;
		if (classNode.signature != null) {
			DecompilerSignatureVisitor sv = new DecompilerSignatureVisitor(0);
			SignatureReader r = new SignatureReader(classNode.signature);
			r.accept(sv);
			genericDeclaration = sv.getDeclaration();
		}

		if (genericDeclaration != null){
			buf.append(genericDeclaration);
		} else {
			addSuperClass(classNode.superName);
			if (isClass) addInterfaces(classNode.interfaces);
		}
		text.add(buf.toString());
		text.add(BLOCK_START);

		appendFields(classNode.fields);

		appendMethods(classNode.methods);
		appendInnerClasses(classNode.innerClasses);


		text.add(BLOCK_END);

		return text;
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

	private void appendMethods(List methods) {
		for (Object method : methods) {
			MethodBlock methodBlock = new MethodBlock((MethodNode)method);
			methodBlock.setClassName(mClassNode.name);
			methodBlock.setClassAccess(mClassNode.access);
			text.add(methodBlock.disassemble());
		}
	}

	//region fields
	protected void appendFields(List<FieldNode> fields) {
		for (FieldNode field : fields) {
			appendAllSingleLineAnnotations(field.visibleAnnotations, field.invisibleAnnotations);
			appendFieldNode(field);
		}
	}

	private void appendFieldNode(FieldNode field) {
		clearBuffer();
		addDeprecatedAnnotationIfNeeded(field.access);
		addAccess(field.access);
		if (containsFlag(field.access, Opcodes.ACC_SYNTHETIC)) {
			addComment("synthetic");
		}
		if (field.signature != null) {
			addFieldSignature(field.signature);
		} else {
			addType(field.desc);
		}
		buf.append(field.name);
		if (field.value != null) {
			buf.append(" = ").append(field.value);
		}
		addStatementEnd();
		text.add(buf.toString());
	}

	private void addFieldSignature(String signature) {
		DecompilerSignatureVisitor sv = new DecompilerSignatureVisitor(0);
		SignatureReader r = new SignatureReader(signature);
		r.acceptType(sv);
		buf.append(sv.getDeclaration()).append(" ");
	}
	//endregion

	protected void appendInnerClasses(List<InnerClassNode> innerClasses) {
		for (InnerClassNode innerClass : innerClasses) {
			InnerClassBlock icb = new InnerClassBlock(innerClass, mClassNode.name);
			text.add(icb.disassemble());
		}
	}
}
