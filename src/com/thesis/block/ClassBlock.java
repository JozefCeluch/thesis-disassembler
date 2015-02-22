package com.thesis.block;

import com.thesis.common.SignatureVisitor;
import com.thesis.common.Util;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class ClassBlock extends Block {
	ClassNode mClassNode;

	public ClassBlock(ClassNode classNode, Block parent) {
		mClassNode = classNode;
		mParent = parent;
	}

	@Override
	public Block disassemble() {

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
			SignatureVisitor sv = new SignatureVisitor(0);
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

		return this;
	}

	private void appendAllSingleLineAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists) {
			text.add(mAnnotationParser.getAnnotations(annotationNodeList, NL));
		}
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
			children.add(methodBlock.disassemble());
		}
	}

	protected void appendFields(List fields) {
		for (Object object : fields) {
			FieldNode field = (FieldNode)object;
			Field fieldBlock = new Field(field, this);
			children.add(fieldBlock.disassemble());
		}
	}

	protected void appendInnerClasses(List innerClasses) {
		for (Object object : innerClasses) {
			InnerClassNode innerClass = (InnerClassNode) object;
			if (shouldAddInnerClass(innerClass)) {
				InnerClassBlock icb = new InnerClassBlock(innerClass, mClassNode.name, this);
				children.add(icb.disassemble());
			}
		}
	}

	private boolean shouldAddInnerClass(InnerClassNode innerClass) {
		return (innerClass.outerName != null && innerClass.innerName != null &&
				innerClass.name.matches(mClassNode.name + "\\$" + innerClass.innerName)) ||
				(innerClass.outerName == null && innerClass.innerName == null
				&& innerClass.name.matches(mClassNode.name + "\\$[0-9]+$"));
	}

	@Override
	public int countParents() {
		if (!hasParent()) {
			return 0;
		}
		return super.countParents();
	}

	@Override
	public void write(Writer writer) throws IOException {
		printList(writer, text);

		for (Block block : children) {
			block.write(writer);
		}

		writer.append(BLOCK_END);
	}
}
