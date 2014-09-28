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

	public ClassBlock(ClassNode classNode, Block parent) {
		mClassNode = classNode;
		mParent = parent;
	}

	public List<Object> disassemble() {

		appendAllSingleLineAnnotations(mClassNode.visibleAnnotations, mClassNode.invisibleAnnotations); //todo

		boolean isClass = false;
		clearBuffer();
		addAccess(mClassNode.access & ~Opcodes.ACC_SUPER);

		if (Util.containsFlag(mClassNode.access, Opcodes.ACC_ANNOTATION)) {
			buf.append("@interface ");
			removeFromBuffer("abstract ");
		} else if (Util.containsFlag(mClassNode.access, Opcodes.ACC_INTERFACE)) {
			buf.append("interface "); // interface is implicitly abstract
			removeFromBuffer("abstract ");
		} else if (!Util.containsFlag(mClassNode.access, Opcodes.ACC_ENUM)) {
			buf.append("class ");
			isClass = true;
		}

		buf.append(Util.removeOuterClasses(mClassNode.name));
		String genericDeclaration = null;
		if (mClassNode.signature != null) {
			DecompilerSignatureVisitor sv = new DecompilerSignatureVisitor(0);
			SignatureReader r = new SignatureReader(mClassNode.signature);
			r.accept(sv);
			genericDeclaration = sv.getDeclaration();
		}

		if (genericDeclaration != null){
			buf.append(genericDeclaration);
		} else {
			addSuperClass(mClassNode.superName);
			if (isClass) addInterfaces(mClassNode.interfaces);
		}
		text.add(buf.toString());
		text.add(BLOCK_START);

		appendFields(mClassNode.fields);
		appendMethods(mClassNode.methods);
		appendInnerClasses(mClassNode.innerClasses);


		appendClassEnd();

		return text;
	}

	private void appendAllSingleLineAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists) {
			text.add(mAnnotationParser.getAnnotations(annotationNodeList, NL));
		}
	}

	private void appendClassEnd() {
		text.add(RIGHT_BRACKET);
	}

	private void addSuperClass(String superName) {
		if (superName != null && !superName.equals("java/lang/Object")) {
			buf.append(" extends ").append(Util.javaObjectName(superName)).append(" ");
		}
	}

	private void addInterfaces(List interfaces) {
		if (interfaces != null && interfaces.size() > 0) {
			buf.append(" implements ");
			for (int i = 0; i < interfaces.size(); i++) {
				addComma(i);
				buf.append(Util.javaObjectName((String) interfaces.get(i)));
			}
		}
	}

	private void appendMethods(List methods) {
		for (Object method : methods) {
			MethodBlock methodBlock = new MethodBlock((MethodNode)method, this);
			methodBlock.setClassName(mClassNode.name);
			methodBlock.setClassAccess(mClassNode.access);
			text.add(methodBlock.disassemble());
		}
	}

	protected void appendFields(List fields) {
		for (Object object : fields) {
			FieldNode field = (FieldNode)object;
			FieldBlock fieldBlock = new FieldBlock(field, this);
			text.add(fieldBlock.disassemble());
		}
	}

	protected void appendInnerClasses(List innerClasses) {
		for (Object innerClass : innerClasses) {
			InnerClassBlock icb = new InnerClassBlock((InnerClassNode)innerClass, mClassNode.name, this);
			text.add(icb.disassemble());
		}
	}

	@Override
	public int countParents() {
		if (!hasParent()) {
			return 0;
		}
		return super.countParents();
	}
}
