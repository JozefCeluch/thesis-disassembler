package com.thesis.translator.handler;

import com.thesis.common.DataType;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.*;
import com.thesis.expression.AssignmentExpression.LeftHandSide;
import com.thesis.translator.ExpressionStack;
import com.thesis.expression.variable.GlobalVariable;
import com.thesis.translator.MethodState;
import org.apache.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

public class FieldInsnNodeHandler extends AbstractHandler {

	private static final Logger LOG = Logger.getLogger(FieldInsnNodeHandler.class);

	public FieldInsnNodeHandler(MethodState state) {
		super(state);
	}

	//	GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD
	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		LOG.debug(logNode(node));
		checkType(node, FieldInsnNode.class);
		ExpressionStack stack = mState.getActiveStack();
		int opCode = node.getOpcode();
		if (opCode == Opcodes.PUTFIELD) {
			Expression value = stack.pop();
			if (!(stack.peek() instanceof PrimaryExpression)) { //TODO expects Primary expression on top because the previous was popped, maybe implementing DUP and POP is necessary
				stack.pop();
			}
			PrimaryExpression owner = (PrimaryExpression)stack.pop();
			DataType ownerType = DataType.getTypeFromObject(owner.getValue().toString());
			GlobalVariable field = new GlobalVariable(((FieldInsnNode)node).name, DataType.getTypeFromDesc(((FieldInsnNode)node).desc), ownerType);
			stack.push(new AssignmentExpression(opCode, new LeftHandSide(opCode,field), value));
		}
		if (opCode == Opcodes.GETFIELD) {
			PrimaryExpression owner = (PrimaryExpression) stack.pop();
			DataType ownerType = DataType.getTypeFromObject(owner.getValue().toString());
			GlobalVariable field = new GlobalVariable(((FieldInsnNode)node).name, DataType.getTypeFromDesc(((FieldInsnNode)node).desc), ownerType);
			stack.push(new VariablePrimaryExpression(opCode, field));
		}
		if (opCode == Opcodes.GETSTATIC) {
			GlobalVariable staticField = new GlobalVariable(((FieldInsnNode)node).name, DataType.getTypeFromDesc(((FieldInsnNode)node).desc), DataType.getType(Type.getObjectType(((FieldInsnNode)node).owner)));
			stack.push(new VariablePrimaryExpression(opCode, staticField));
		}
		if (opCode == Opcodes.PUTSTATIC) {
			Expression value = stack.pop();
			GlobalVariable field = new GlobalVariable(((FieldInsnNode)node).name, DataType.getTypeFromDesc(((FieldInsnNode)node).desc), DataType.getTypeFromObject(((FieldInsnNode)node).owner));
			stack.push(new AssignmentExpression(opCode, new LeftHandSide(opCode, field), value));
		}
	}
}
