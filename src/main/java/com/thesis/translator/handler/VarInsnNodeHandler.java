package com.thesis.translator.handler;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.AssignmentExpression;
import com.thesis.expression.AssignmentExpression.LeftHandSide;
import com.thesis.expression.Expression;
import com.thesis.expression.PrimaryExpression;
import com.thesis.expression.VariablePrimaryExpression;
import com.thesis.translator.ExpressionStack;
import com.thesis.expression.variable.LocalVariable;
import com.thesis.translator.MethodState;
import org.apache.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET
 */
public class VarInsnNodeHandler extends AbstractHandler {
	private static final Logger LOG = Logger.getLogger(VarInsnNodeHandler.class);

	public VarInsnNodeHandler(MethodState state) {
		super(state);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		checkType(node, VarInsnNode.class);

		ExpressionStack stack = mState.getActiveStack();
		int varNum = ((VarInsnNode) node).var;
		int opCode = node.getOpcode();
		if (Util.isBetween(opCode, Opcodes.ILOAD, Opcodes.ALOAD)) {
			LocalVariable var = mState.getLocalVariable(varNum);
			stack.push(new VariablePrimaryExpression(opCode, var));
		}
		if (Util.isBetween(opCode, Opcodes.ISTORE, Opcodes.ASTORE)) {
			LocalVariable localVar = mState.getLocalVariable(varNum);
			if (localVar == null) {
				localVar = new LocalVariable(Util.VARIABLE_NAME_BASE + varNum, DataType.UNKNOWN, varNum);
				mState.addLocalVariable(varNum, localVar); //TODO set type according to the instruction
			}
			Expression rightSide = null;
			if (mState.getTryCatchManager().hasCatchBlockStart(mState.getCurrentLabel()) && !(mState.getActiveStack().peek() instanceof VariablePrimaryExpression)) {
				rightSide = new PrimaryExpression(localVar.getType().toString(), localVar.getType());
			}
			stack.push(new AssignmentExpression(opCode, new LeftHandSide(opCode, localVar), rightSide));

		}
		// RET is deprecated since Java 6
	}
}
