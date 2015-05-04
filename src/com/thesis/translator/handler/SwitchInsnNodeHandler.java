package com.thesis.translator.handler;

import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.BreakExpression;
import com.thesis.expression.JumpExpression;
import com.thesis.expression.SwitchExpression;
import com.thesis.expression.UnconditionalJump;
import com.thesis.expression.stack.ExpressionStack;
import com.thesis.translator.MethodState;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import java.util.HashMap;
import java.util.Map;

/**
 * TABLESWITCH, LOOKUPSWITCH
 */
public class SwitchInsnNodeHandler extends AbstractHandler {

	public SwitchInsnNodeHandler(MethodState state, OnNodeMoveListener moveListener) {
		super(state, moveListener);
	}

	@Override
	public void handle(AbstractInsnNode node) throws IncorrectNodeException {
		super.handle(node);
		if (node instanceof TableSwitchInsnNode) {
			visitTableSwitchInsnNode((TableSwitchInsnNode) node);
		} else if (node instanceof LookupSwitchInsnNode) {
			visitLookupSwitchInsnNode((LookupSwitchInsnNode) node);
		} else {
			throw new IncorrectNodeException("Incorrect node type, expected switch but was " + node.getClass().getSimpleName());
		}
	}

	private void visitTableSwitchInsnNode(TableSwitchInsnNode node) {
		ExpressionStack stack = mState.getActiveStack();
		int defaultLabel = stack.getLabelId(node.dflt.getLabel());

		Map<Integer, String> labelCaseMap = new HashMap<>();

		for (int i = 0; i <= node.max - node.min; i++) {
			int labelId = stack.getLabelId(((LabelNode) node.labels.get(i)).getLabel());
			String caseKey = String.valueOf(node.min + i);
			labelCaseMap.put(labelId, caseKey);
		}
		labelCaseMap.put(defaultLabel, SwitchExpression.CaseExpression.DEFAULT);

		SwitchExpression switchExp = new SwitchExpression(node.getOpcode());
		mState.moveNode();
		updateSwitchWithCases(switchExp, defaultLabel, labelCaseMap);
		stack.push(switchExp);
	}

	private void visitLookupSwitchInsnNode(LookupSwitchInsnNode node) {
		ExpressionStack stack = mState.getActiveStack();
		int defaultLabel = stack.getLabelId(node.dflt.getLabel());
		Map<Integer, String> labelCaseMap = new HashMap<>();
		for (int i = 0; i < node.labels.size(); i++) {
			int labelId = stack.getLabelId(((LabelNode) node.labels.get(i)).getLabel());
			String caseKey = String.valueOf(node.keys.get(i));
			labelCaseMap.put(labelId, caseKey);
		}
		labelCaseMap.put(defaultLabel, SwitchExpression.CaseExpression.DEFAULT);

		SwitchExpression switchExp = new SwitchExpression(node.getOpcode());
		mState.moveNode();
		updateSwitchWithCases(switchExp, defaultLabel, labelCaseMap);
		stack.push(switchExp);
	}

	private void updateSwitchWithCases(SwitchExpression switchExp, int defaultLabel, Map<Integer, String> labelCaseMap) {
		int switchEndLabel = JumpExpression.NO_DESTINATION;
		ExpressionStack caseStack = null;
		SwitchExpression.CaseExpression caseExpression = null;

		while (!mState.isLabelVisited(switchEndLabel) && mState.getCurrentNode() != null) {
			if (caseStack == null) {
				caseStack = mState.startNewStack();
			}

			nodeMoved();
			mState.moveNode();

			if (labelCaseMap.containsKey(mState.getCurrentLabel()) && caseExpression == null) {
				caseExpression = new SwitchExpression.CaseExpression(labelCaseMap.get(mState.getCurrentLabel()), mState.getCurrentLabel(), defaultLabel, caseStack);
				switchExp.addCase(caseExpression);
			}

			if (caseExpression != null && caseExpression.getLabel() != mState.getCurrentLabel() && (labelCaseMap.containsKey(mState.getCurrentLabel()) || mState.isLabelVisited(switchEndLabel))) {
				if (caseStack.peek() instanceof UnconditionalJump) {
					UnconditionalJump jump = (UnconditionalJump) caseStack.pop();
					if (switchEndLabel == JumpExpression.NO_DESTINATION) {
						switchEndLabel = jump.getJumpDestination();
					}
					caseStack.push(new BreakExpression(jump));
				}
				mState.finishStack();
				caseStack = null;
				caseExpression = null;
			}
		}
	}
}
