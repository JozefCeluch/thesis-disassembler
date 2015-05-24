package com.thesis.translator.handler;

import com.thesis.common.DataType;
import com.thesis.common.Util;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.*;
import com.thesis.translator.ExpressionStack;
import com.thesis.translator.MethodState;
import org.apache.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * Handles the {@link JumpInsnNode}
 * <p>
 * instructions:
 * IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IFNULL or IFNONNULL
 * IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE,
 * GOTO, JSR (deprecated since Java 6).
 */
public class JumpInsnNodeHandler extends AbstractHandler {
	private static final Logger LOG = Logger.getLogger(JumpInsnNodeHandler.class);

	public JumpInsnNodeHandler(MethodState state, OnNodeMovedListener onMovedListener) {
		super(state, onMovedListener);
	}

	/**
	 * IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IFNULL or IFNONNULL
	 * IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE,
	 * GOTO, JSR (deprecated since Java 6).
	 */
	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		LOG.debug(logNode(node));
		checkType(node, JumpInsnNode.class);

		ExpressionStack stack = mState.getActiveStack();
		JumpExpression exp = makeConditionalExpression((JumpInsnNode) node, stack);

		if (exp instanceof UnconditionalJump) {
			int frameIndex = stack.getExpressionIndexOfFrame(exp.getJumpDestination());
			if (frameIndex != -1) {
				exp.setThenBranch(stack.substack(frameIndex, stack.size()));
				mState.startNewStack();
				mState.replaceActiveStack(exp.getThenBranch());
			} else {
				stack.push(exp);
				return;
			}
		} else {
			exp.setThenBranch(mState.startNewStack());
		}

		if (exp.getStartFrameLocation() == JumpExpression.NO_DESTINATION && mState.getFrameLabel() != JumpExpression.NO_DESTINATION) {
			exp.setStartFrameLocation(mState.getFrameLabel());
			mState.setFrameLabel(JumpExpression.NO_DESTINATION);
		}
		while (!mState.isLabelVisited(exp.getJumpDestination())) {
			mState.moveNode();
			if (isConditionalJump(mState.getCurrentNode())) {
				if (checkLogicGateExpressionIsOnTop(exp)) {
					exp = new LogicGateExpression(exp, (JumpExpression) exp.getThenBranch().pop());
					mState.replaceActiveStack(exp.getThenBranch());
				}
			}
			nodeMoved();

			if (isEndOfThenBlock(mState.getCurrentNode()) ) {
				int gotoJumpDestination = mState.getActiveStack().getLabelId(((JumpInsnNode) mState.getCurrentNode()).label.getLabel());
				exp.setElseBranchEnd(gotoJumpDestination);
				exp.updateThenBranchType();
				if (exp.getJumpDestination() == gotoJumpDestination) {
					break;
					/*break is needed to correctly recognize all cases in switches with Strings because consists of
					* non-standard switch where default case is called after each standard case*/
				}
			}
			if (mState.getTryCatchManager().hasCatchBlockStart(mState.getCurrentLabel())) {
				break;
			}
		}
		mState.finishStack();

		Expression catchBlockEnd = null;
		if (exp.hasEmptyElseBranch() && !exp.isLoop()) {
			exp.setElseBranch(mState.startNewStack());
			while(!mState.isLabelVisited(exp.getElseBranchEnd())) { //mCurrentLabel != exp.getElseBranchEnd()
				mState.moveNode();
				nodeMoved();
				if (mState.getTryCatchManager().hasCatchBlockStart(mState.getCurrentLabel())) {
					catchBlockEnd = exp.getElseBranch().pop();
					break;
				}
			}
			exp.updateElseBranchType();
			mState.finishStack();
		}

		if (exp.isTernaryExpression()) {
			mState.getActiveStack().push(new TernaryExpression(exp));
		} else {
			mState.getActiveStack().push(exp);
		}
		mState.updateCurrentLabel(mState.getCurrentLabel());
		if (catchBlockEnd != null) {
			mState.getActiveStack().push(catchBlockEnd);
		}
	}

	private boolean isEndOfThenBlock(AbstractInsnNode movedNode) {
		return movedNode instanceof JumpInsnNode && movedNode.getOpcode() == Opcodes.GOTO;
	}

	private boolean isConditionalJump(AbstractInsnNode movedNode) {
		return movedNode instanceof JumpInsnNode && movedNode.getOpcode() != Opcodes.GOTO;
	}

	private boolean checkLogicGateExpressionIsOnTop(JumpExpression exp) throws IncorrectNodeException {
		ExpressionStack thenBranchBackup = exp.getThenBranch().duplicate();
		JumpExpression innerExp = makeConditionalExpression((JumpInsnNode) mState.getCurrentNode(), exp.getThenBranch());
		exp.getThenBranch().push(innerExp);
		if (exp.containsLogicGateExpression()) {
			mState.moveNode();
			return true;
		} else {
			exp.setThenBranch(thenBranchBackup);
			mState.replaceActiveStack(exp.getThenBranch());

			handle(mState.getCurrentNode());
			return exp.containsLogicGateExpression();
		}
	}

	private JumpExpression makeConditionalExpression(JumpInsnNode node, ExpressionStack stack) {
		JumpExpression exp = null;

		int jumpDestination = stack.getLabelId(node.label.getLabel());
		int opCode = node.getOpcode();
		LOG.debug("jump destination L" + jumpDestination);

		if (Util.isBetween(opCode, Opcodes.IF_ICMPEQ, Opcodes.IF_ACMPNE)) {
			exp = new MultiConditional(opCode, jumpDestination, stack.pop(), stack.pop());
		} else if (Util.isBetween(opCode, Opcodes.IFEQ, Opcodes.IFLE)) {
			Expression stackTop = stack.peek();
			if (stackTop instanceof MultiConditional && !((MultiConditional) stackTop).isJumpDestinationSet()) {
				exp = (MultiConditional) stack.pop();
				exp.setJumpDestination(jumpDestination);
				exp.setOpCode(node.getOpcode());
			} else {
				exp = new SingleConditional(opCode, jumpDestination, stack.pop());
			}
		} else if (Util.isBetween(opCode, Opcodes.IFNULL, Opcodes.IFNONNULL)) {
			exp = new MultiConditional(opCode, jumpDestination, new PrimaryExpression("null", DataType.UNKNOWN), stack.pop());
		} else if (opCode == Opcodes.GOTO) {
			exp = new UnconditionalJump(opCode, jumpDestination);
		}
		if (opCode != Opcodes.GOTO && exp != null && node.getNext() != null && node.getNext() instanceof LabelNode) {
			exp.setThenBranchStart(stack.getLabelId(((LabelNode) node.getNext()).getLabel()));
		}
		return exp;
	}
}
