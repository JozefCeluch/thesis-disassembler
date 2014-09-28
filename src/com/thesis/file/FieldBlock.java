package com.thesis.file;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

public class FieldBlock extends StatementBlock {

	private FieldNode mFieldNode;

	public FieldBlock(FieldNode fieldNode, Block parent) {
		mFieldNode = fieldNode;
		mParent = parent;
	}

	public String disassemble() {
		appendAllSingleLineAnnotations(mFieldNode.visibleAnnotations, mFieldNode.invisibleAnnotations);
		clearBuffer();
		addDeprecatedAnnotationIfNeeded(mFieldNode.access);
		addAccess(mFieldNode.access);
		if (Util.containsFlag(mFieldNode.access, Opcodes.ACC_SYNTHETIC)) {
			addComment("synthetic");
		}
		if (mFieldNode.signature != null) {
			addFieldSignature(mFieldNode.signature);
		} else {
			addType(mFieldNode.desc);
		}
		buf.append(mFieldNode.name);
		if (mFieldNode.value != null) {
			buf.append(" = ").append(mFieldNode.value);
		}
		addStatementEnd();
		return buf.toString();
	}

	private void addFieldSignature(String signature) {
		DecompilerSignatureVisitor sv = new DecompilerSignatureVisitor(0);
		SignatureReader r = new SignatureReader(signature);
		r.acceptType(sv);
		buf.append(sv.getDeclaration()).append(" ");
	}

	private void appendAllSingleLineAnnotations(List... annotationLists){
		for (List annotationNodeList : annotationLists) {
			text.add(mAnnotationParser.getAnnotations(annotationNodeList, NL));
		}
	}
}
