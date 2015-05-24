package com.thesis.translator;

import com.thesis.expression.*;

import java.util.Stack;

/**
 * Enhances the {@link ExpressionStack} to better handle loops
 * <p>
 * The class is used to recognize the following patterns:
 *  - presence of do-while loops
 *  - presence of break and continue statement
 *  - general loop detection that was not discovered properly during decompilation (like while(true) loop)
 */
public class LoopEnhancer implements StackEnhancer {

	@Override
	public void enhance(ExpressionStack expressionStack, Stack<ExpressionStack.Item> stack) {
		for (int i = 0; i < expressionStack.size(); i++) {
			Expression exp = expressionStack.get(i);
			if (exp instanceof JumpExpression) {
				updateDoLoops((JumpExpression) exp, i, expressionStack, stack);
			}
		}

		for (int i = 0; i < stack.size(); i++) {
			Expression exp = stack.get(i).getExpression();
			if (exp instanceof JumpExpression) {
				int loopStartLabel = stack.get(i).getLabelId();
				int nextLabel = i < stack.size() - 1 ? stack.get(i+1).getLabelId() : -2;
				fixLoopDetection((JumpExpression) exp, loopStartLabel, stack.get(i).getExpression());
				improveBreakAndContinue((JumpExpression) exp, nextLabel, (JumpExpression) exp);
			}
		}
	}

	private void updateDoLoops(JumpExpression exp, int currentStackPosition, ExpressionStack expressionStack, Stack<ExpressionStack.Item> stack) {
		if ((exp.getThenBranch() != null && !exp.getThenBranch().isEmpty()) || (exp.getElseBranch() != null && !exp.getElseBranch().isEmpty())) return;
		for (int stackPosition = 0; stackPosition < currentStackPosition; stackPosition++) {
			if (stack.get(stackPosition).getLabelId() == exp.getJumpDestination()) {
				exp.setLoopType(JumpExpression.LoopType.DO_WHILE);
				exp.setStartFrameLocation(exp.getJumpDestination());
				exp.getThenBranch().addAll(expressionStack.substack(stackPosition, currentStackPosition));
				exp.negate();
				break;
			}
		}
	}

	private void fixLoopDetection(JumpExpression topLevelExp, int loopStartLabel, Expression stackItem) {
		if (!(stackItem instanceof JumpExpression) || ((JumpExpression) stackItem).getThenBranch() == null) {
			return;
		}
		JumpExpression expression = ((JumpExpression) stackItem);
		for (int i = 0; i < expression.getThenBranch().size(); i++) {
			Expression exp = expression.getThenBranch().get(i);
			fixLoopDetection(topLevelExp, loopStartLabel, exp);
			if (exp instanceof JumpExpression && ((JumpExpression) exp).getJumpDestination() == loopStartLabel) {
				topLevelExp.setElseBranchEnd(((JumpExpression) exp).getJumpDestination());
			}
		}

		if (((JumpExpression) stackItem).getElseBranch() != null) {
			for (int i = 0; i < expression.getElseBranch().size(); i++) {
				Expression exp = expression.getElseBranch().get(i);
				fixLoopDetection(topLevelExp, loopStartLabel, exp);
				if (exp instanceof JumpExpression && ((JumpExpression) exp).getJumpDestination() == loopStartLabel) {
					topLevelExp.setElseBranchEnd(((JumpExpression) exp).getJumpDestination());
				}
			}
		}
	}

	private void improveBreakAndContinue(JumpExpression exp, int nextLabel, JumpExpression topLevelExpression) {
		ExpressionStack thenBranch;
		ExpressionStack elseBranch;

		if (exp.getThenBranch() != null) {
			thenBranch = exp.getThenBranch();
			for (int i = 0; i < thenBranch.size(); i++) {
				Expression innerExp = thenBranch.get(i);
				if (innerExp instanceof JumpExpression) {
					improveBreakAndContinue((JumpExpression) innerExp, nextLabel, topLevelExpression);
				}
			}
			addBreak(nextLabel, topLevelExpression, thenBranch);
			addContinue(exp, topLevelExpression, thenBranch);
		}

		if (exp.getElseBranch() != null) {
			elseBranch = exp.getElseBranch();
			for (int i = 0; i < elseBranch.size(); i++) {
				Expression innerExp = elseBranch.get(i);
				if (innerExp instanceof JumpExpression) {
					improveBreakAndContinue((JumpExpression) innerExp, nextLabel, topLevelExpression);
				}
			}

			addBreak(nextLabel, topLevelExpression, elseBranch);
			addContinue(exp, topLevelExpression, elseBranch);
		}
	}

	private void addBreak(int nextLabel, JumpExpression topLevelExpression, ExpressionStack branch) {
		if (branch.isEmpty()) return;
		Expression lastItem = branch.get(branch.size() - 1);
		if (lastItem instanceof UnconditionalJump) {
			ExpressionStack.Item lastElseBranchItem = branch.getItem(branch.size() - 1);
			if (topLevelExpression.isLoop() &&
					((UnconditionalJump) lastElseBranchItem.getExpression()).getJumpDestination() == nextLabel) {
				lastElseBranchItem.setExpression(new BreakExpression((UnconditionalJump) lastElseBranchItem.getExpression()));
				topLevelExpression.setElseBranchEnd(topLevelExpression.getStartFrameLocation());
			}
		}
	}

	private void addContinue(JumpExpression currentExp, JumpExpression topLevelExp, ExpressionStack branch) {
		if (currentExp.equals(topLevelExp) || branch.isEmpty()) return;

		Expression topLevelLastItem = topLevelExp.getThenBranch().get(topLevelExp.getThenBranch().size() - 1);
		ExpressionStack.Item lastItem = branch.getItem(branch.size() - 1);
		if (lastItem.getExpression() instanceof UnconditionalJump && topLevelLastItem instanceof UnconditionalJump) {
			if (topLevelExp.isLoop() && ((UnconditionalJump) lastItem.getExpression()).getJumpDestination() == topLevelExp.getStartFrameLocation()) {
				lastItem.setExpression(new ContinueExpression((UnconditionalJump) lastItem.getExpression()));
			}
		}
	}
}
