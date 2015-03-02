package com.thesis.block;

import com.thesis.LocalVariable;
import com.thesis.common.DataType;
import com.thesis.common.SignatureVisitor;
import com.thesis.expression.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Field extends Statement {

	private FieldNode mFieldNode;
	private List<Object> mAnnotations;
	private String mAccessFlags;
	private Expression mExpression;
	private DataType mType;

	public Field(FieldNode fieldNode, Block parent) {
		super(0); //todo line
		mFieldNode = fieldNode;
		mParent = parent;
	}

	@Override
	public Block disassemble() {
		mAnnotations = getSingleLineAnnotations(mFieldNode.visibleAnnotations, mFieldNode.invisibleAnnotations);

		mAccessFlags = getAccessFlags();

		mType = getType(mFieldNode.desc, mFieldNode.signature);
		LocalVariable variable = new LocalVariable(mFieldNode.name, mType, 0);
		if (mFieldNode.value != null) {
			mExpression = new AssignmentExpression(null, new LeftHandSide(null, variable), new PrimaryExpression(mFieldNode.value, mType));
		} else {
			mExpression = new VariableDeclarationExpression(variable);
		}
		return this;
	}

	private String getAccessFlags() {
		clearBuffer();
		addAccess(mFieldNode.access);
		return buf.toString();
	}

	private DataType getType(String desc, String signature) {
		if (signature != null) {
			SignatureVisitor sv = new SignatureVisitor(0);
			SignatureReader r = new SignatureReader(signature);
			r.acceptType(sv);
			return DataType.getType(sv.getDeclaration());
		} else {
			return DataType.getType(Type.getType(desc));
		}
	}

	@Override
	public void write(Writer writer) throws IOException {
		printList(writer, mAnnotations);
		writer.write(mAccessFlags);
		if (mExpression instanceof AssignmentExpression) {
			writer.append(mType.print()).append(" ");
		}
		mExpression.write(writer);
		writer.write(STATEMENT_END_NL);
	}
}
