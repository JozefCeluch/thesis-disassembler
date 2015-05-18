package com.thesis.translator.handler;

import com.thesis.common.DataType;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.AssignmentExpression;
import com.thesis.expression.AssignmentExpression.LeftHandSide;
import com.thesis.expression.PrimaryExpression;
import com.thesis.expression.UnaryExpression;
import com.thesis.translator.ExpressionStack;
import com.thesis.expression.variable.LocalVariable;
import com.thesis.translator.MethodState;
import org.apache.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;

/**
 * IINC
 */
public class IincInsnNodeHandler extends AbstractHandler {

	private static final Logger LOG = Logger.getLogger(IincInsnNodeHandler.class);

	public IincInsnNodeHandler(MethodState state) {
		super(state);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		LOG.debug(logNode(node));
		checkType(node, IincInsnNode.class);

		ExpressionStack stack = mState.getActiveStack();
		LocalVariable variable = mState.getLocalVariable(((IincInsnNode) node).var);
		
		UnaryExpression.OpPosition opPosition = getUnaryOperandPosition(node);
		if (opPosition != null) {
			stack.push(new UnaryExpression(node.getOpcode(), variable, DataType.INT, opPosition));
		} else {
			stack.push(new AssignmentExpression(node.getOpcode(), new LeftHandSide(node.getOpcode(), variable),
					new PrimaryExpression(node.getOpcode(), ((IincInsnNode) node).incr, DataType.INT)));
		}
	}

	private UnaryExpression.OpPosition getUnaryOperandPosition(AbstractInsnNode node) {
		if (node.getPrevious() != null && node.getPrevious().getOpcode() == Opcodes.ILOAD) {
			return UnaryExpression.OpPosition.POSTFIX;
		} else if (node.getNext() != null && node.getNext().getOpcode() == Opcodes.ILOAD) {
			return UnaryExpression.OpPosition.PREFIX;
		}
		return null;
	}
}
