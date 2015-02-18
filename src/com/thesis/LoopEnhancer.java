package com.thesis;

import com.thesis.expression.*;

import java.util.List;
import java.util.Stack;

public class LoopEnhancer implements StackEnhancer {

	@Override
	public void enhance(ExpressionStack expressionStack, Stack<StackItem> stack) {
		for (int i = 0; i < expressionStack.size(); i++) {
			Expression exp = expressionStack.get(i);
			if (exp instanceof ConditionalExpression) {
				updateDoLoops((ConditionalExpression) exp, i, expressionStack, stack);
			}
		}

		for (int i = 0; i < stack.size(); i++) {
			Expression exp = stack.get(i).getExpression();
			if (exp instanceof ConditionalExpression) {
				int loopStartLabel = stack.get(i).getLabelId();
				int nextLabel = i < stack.size() - 1 ? stack.get(i+1).getLabelId() : -2;
				fixLoopDetection((ConditionalExpression) exp, loopStartLabel, stack.get(i));
				improveBreakAndContinue((ConditionalExpression) exp, nextLabel, (ConditionalExpression) exp);
			}
		}
	}

	private void updateDoLoops(ConditionalExpression exp, int currentStackPosition, ExpressionStack expressionStack, Stack<StackItem> stack) {
		if ((exp.getThenBranch() != null && !exp.getThenBranch().isEmpty()) || (exp.getElseBranch() != null && !exp.getElseBranch().isEmpty())) return;
		for (int stackPosition = 0; stackPosition < currentStackPosition; stackPosition++) {
			if (stack.get(stackPosition).getLabelId() == exp.getJumpDestination()) {
				exp.setLoopType(ConditionalExpression.LoopType.DO_WHILE);
				exp.setStartFrameLocation(exp.getJumpDestination());
				exp.getThenBranch().addAll(expressionStack.substack(stackPosition, currentStackPosition));
				exp.negate();
				break;
			}
		}
	}

	private void fixLoopDetection(ConditionalExpression topLevelExp, int loopStartLabel, StackItem stackItem) {
		if (!(stackItem.getExpression() instanceof ConditionalExpression) || ((ConditionalExpression) stackItem.getExpression()).getThenBranch() == null) {
			return;
		}
		ConditionalExpression expression = ((ConditionalExpression) stackItem.getExpression());
		for (int i = 0; i < expression.getThenBranch().getAll().size(); i++) {
			StackItem item = expression.getThenBranch().getAll().get(i);
			fixLoopDetection(topLevelExp, loopStartLabel, item);
			if (item.getExpression() instanceof ConditionalExpression && ((ConditionalExpression) item.getExpression()).getJumpDestination() == loopStartLabel) {
				topLevelExp.setElseBranchEnd(((ConditionalExpression) item.getExpression()).getJumpDestination());
			}
		}

		if (((ConditionalExpression) stackItem.getExpression()).getElseBranch() != null) {
			for (int i = 0; i < expression.getElseBranch().getAll().size(); i++) {
				StackItem item = expression.getElseBranch().getAll().get(i);
				fixLoopDetection(topLevelExp, loopStartLabel, item);
				if (item.getExpression() instanceof ConditionalExpression && ((ConditionalExpression) item.getExpression()).getJumpDestination() == loopStartLabel) {
					topLevelExp.setElseBranchEnd(((ConditionalExpression) item.getExpression()).getJumpDestination());
				}
			}
		}
	}

	private void improveBreakAndContinue(ConditionalExpression exp, int nextLabel, ConditionalExpression topLevelExpression) {
		List<StackItem> thenBranch;
		List<StackItem> elseBranch;

		if (exp.getThenBranch() != null) {
			thenBranch = exp.getThenBranch().getAll();
			for (StackItem innerExp : thenBranch) {
				if (innerExp.getExpression() instanceof ConditionalExpression) {
					improveBreakAndContinue((ConditionalExpression) innerExp.getExpression(), nextLabel, topLevelExpression);
				}
			}
			addBreak(nextLabel, topLevelExpression, thenBranch);
			addContinue(exp, topLevelExpression, thenBranch);
		}

		if (exp.getElseBranch() != null) {
			elseBranch = exp.getElseBranch().getAll();
			for (StackItem innerExp : elseBranch) {
				if (innerExp.getExpression() instanceof ConditionalExpression) {
					improveBreakAndContinue((ConditionalExpression) innerExp.getExpression(), nextLabel, topLevelExpression);
				}
			}

			addBreak(nextLabel, topLevelExpression, elseBranch);
			addContinue(exp, topLevelExpression, elseBranch);
		}
	}

	private void addBreak(int nextLabel, ConditionalExpression topLevelExpression, List<StackItem> branch) {
		if (branch.isEmpty()) return;
		StackItem lastItem = branch.get(branch.size() - 1);
		if (lastItem.getExpression() instanceof UnconditionalJump) {
			StackItem lastElseBranchItem = branch.get(branch.size() - 1);
			if (topLevelExpression.isLoop() &&
					((UnconditionalJump) lastElseBranchItem.getExpression()).getJumpDestination() == nextLabel) {
				lastElseBranchItem.setExpression(new BreakExpression((UnconditionalJump) lastElseBranchItem.getExpression()));
				topLevelExpression.setElseBranchEnd(topLevelExpression.getStartFrameLocation());
			}
		}
	}

	private void addContinue(ConditionalExpression currentExp, ConditionalExpression topLevelExp, List<StackItem> branch) {
		if (currentExp.equals(topLevelExp) || branch.isEmpty()) return;

		StackItem topLevelLastItem = topLevelExp.getThenBranch().getAll().get(topLevelExp.getThenBranch().size() - 1);
		StackItem lastItem = branch.get(branch.size() - 1);
		if (lastItem.getExpression() instanceof UnconditionalJump && topLevelLastItem.getExpression() instanceof UnconditionalJump) {
			if (topLevelExp.isLoop() && ((UnconditionalJump) lastItem.getExpression()).getJumpDestination() == topLevelExp.getStartFrameLocation()) {
				lastItem.setExpression(new ContinueExpression((UnconditionalJump) lastItem.getExpression()));
			}
		}
	}
}
