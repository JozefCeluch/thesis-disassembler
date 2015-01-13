package com.thesis.block;

import com.thesis.common.SignatureVisitor;
import com.thesis.common.Util;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Field extends Statement {

	private FieldNode mFieldNode;

	public Field(FieldNode fieldNode, Block parent) {
		super(0); //todo line
		mFieldNode = fieldNode;
		mParent = parent;
	}

	@Override
	public Block disassemble() {
		appendAllSingleLineAnnotations(mFieldNode.visibleAnnotations, mFieldNode.invisibleAnnotations);
		clearBuffer();
		addDeprecatedAnnotationIfNeeded(mFieldNode.access);
		addAccess(mFieldNode.access);
		if (Util.containsFlag(mFieldNode.access, Opcodes.ACC_SYNTHETIC)) {
			addComment("synthetic");
		}
		//TODO store fields in a list of GlobalVariable objects
		String type = getType(mFieldNode.desc, mFieldNode.signature);
		buf.append(type).append(" ");
		buf.append(mFieldNode.name);
		if (mFieldNode.value != null) {
			buf.append(" = ").append("java.lang.String".equals(type) ? "\"" : "").append(mFieldNode.value).append("java.lang.String".equals(type) ? "\"" : "");
		}
		addStatementEnd();
		text.add(buf.toString());
		return this;
	}

	private String getType(String desc, String signature) {
		if (signature != null) {
			SignatureVisitor sv = new SignatureVisitor(0);
			SignatureReader r = new SignatureReader(signature);
			r.acceptType(sv);
			return sv.getDeclaration();
		} else {
			return Util.getType(desc).print();
		}
	}

	private void appendAllSingleLineAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists) {
			text.add(mAnnotationParser.getAnnotations(annotationNodeList, NL));
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		printList(writer, text);
	}
}
