package com.thesis.translator.handler;

import com.thesis.common.DataType;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.PrimaryExpression;
import com.thesis.translator.ExpressionStack;
import com.thesis.translator.MethodState;
import org.apache.log4j.Logger;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class LdcInsnNodeHandler extends AbstractHandler {
	private static final Logger LOG = Logger.getLogger(LdcInsnNodeHandler.class);

	public LdcInsnNodeHandler(MethodState state) {
		super(state);
	}

	// LDC
	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		LOG.debug(logNode(node));
		checkType(node, LdcInsnNode.class);
		ExpressionStack stack = mState.getActiveStack();
		DataType type;
		Object constant = ((LdcInsnNode)node).cst;
		if (constant instanceof Integer) {
			type = DataType.INT;
		} else if (constant instanceof Float) {
			type = DataType.FLOAT;
		} else if (constant instanceof Double) {
			type = DataType.DOUBLE;
		} else if (constant instanceof Long) {
			type = DataType.LONG;
		} else if (constant instanceof String) {
			type = DataType.getTypeFromObject("java.lang.String");
		} else {
			constant = DataType.getType((Type) constant); //todo think is this correct?
			type = DataType.getTypeFromObject("java.lang.Class");
		}
		stack.push(new PrimaryExpression(node.getOpcode(), constant, type));
	}

}
