package com.thesis.block;

import com.thesis.expression.variable.LocalVariable;
import com.thesis.common.DataType;
import com.thesis.common.SignatureVisitor;
import com.thesis.expression.*;
import com.thesis.expression.AssignmentExpression.LeftHandSide;
import com.thesis.statement.Statement;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class FieldBlock extends Block {

	private FieldNode mFieldNode;
	private List<Object> mAnnotations;
	private String mAccessFlags;

	public FieldBlock(FieldNode fieldNode, Block parent) {
		mFieldNode = fieldNode;
		mParent = parent;
	}

	@Override
	public Block disassemble() {
		mAnnotations = getSingleLineAnnotations(mFieldNode.visibleAnnotations, mFieldNode.invisibleAnnotations);

		mAccessFlags = getAccessFlags();

		DataType type = getType(mFieldNode.desc, mFieldNode.signature);
		LocalVariable variable = new LocalVariable(mFieldNode.name, type, 0);
		Expression expression;
		if (mFieldNode.value != null) {
			//the field is final
			expression = new AssignmentExpression(0, new LeftHandSide(0, variable), new PrimaryExpression(mFieldNode.value, type));
			((AssignmentExpression) expression).setPrintType(true);
		} else {
			expression = new VariableDeclarationExpression(variable);
		}
		children.add(new Statement(expression, 0));
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
		children.get(0).write(writer);
	}
}
