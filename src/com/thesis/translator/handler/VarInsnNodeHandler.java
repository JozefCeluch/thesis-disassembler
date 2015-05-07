package com.thesis.translator.handler;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.AssignmentExpression;
import com.thesis.expression.AssignmentExpression.LeftHandSide;
import com.thesis.expression.VariablePrimaryExpression;
import com.thesis.translator.ExpressionStack;
import com.thesis.expression.variable.LocalVariable;
import com.thesis.translator.MethodState;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Map;

/**
 * ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET
 */
public class VarInsnNodeHandler extends AbstractHandler {

	public VarInsnNodeHandler(MethodState state) {
		super(state);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		checkType(node, VarInsnNode.class);

		ExpressionStack stack = mState.getActiveStack();
		Map<Integer, LocalVariable> localVars = mState.getLocalVariables();
		int varNum = ((VarInsnNode) node).var;
		int opCode = node.getOpcode();
		if (Util.isBetween(opCode, Opcodes.ILOAD, Opcodes.ALOAD)) {
			LocalVariable var = localVars.get(varNum);
			stack.push(new VariablePrimaryExpression(opCode, var));
		}
		if (Util.isBetween(opCode, Opcodes.ISTORE, Opcodes.ASTORE)) {
			if (!localVars.containsKey(varNum)) {
				localVars.put(varNum, new LocalVariable("var" + varNum, DataType.UNKNOWN, varNum)); //TODO set type according to the instruction
			}
			LocalVariable localVar = localVars.get(varNum);
			stack.push(new AssignmentExpression(opCode, new LeftHandSide(opCode, localVar)));
		}
		// RET is deprecated since Java 6
	}
}