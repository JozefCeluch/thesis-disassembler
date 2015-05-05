package com.thesis.block;

import com.thesis.expression.variable.LocalVariable;
import com.thesis.common.DataType;
import com.thesis.common.SignatureVisitor;
import com.thesis.expression.*;
import com.thesis.expression.AssignmentExpression.LeftHandSide;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class FieldBlock extends Block {

	private FieldNode mFieldNode;
	private List<Object> mAnnotations;
	private String mAccessFlags;
	private Expression mExpression;
	private DataType mType;

	public FieldBlock(FieldNode fieldNode, Block parent) {
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
			mExpression = new AssignmentExpression(0, new LeftHandSide(0, variable), new PrimaryExpression(mFieldNode.value, mType));
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
			return DataType.getTypeFromObject(sv.getDeclaration());
		} else {
			return DataType.getTypeFromDesc(desc);
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
		writer.write(";\n");
	}
}
