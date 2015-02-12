package com.thesis;

import com.thesis.expression.*;

import java.util.List;
import java.util.Stack;

public class LoopEnhancer implements StackEnhancer {

	@Override
	public void enhance(Stack<StackItem> stack) {
		for (int i = 0; i < stack.size(); i++) {
			Expression exp = stack.get(i).getExpression();
			if (exp instanceof ConditionalExpression) {
				int loopStartLabel = stack.get(i).getLabelId();
				int nextLabel = i < stack.size() - 1 ? stack.get(i+1).getLabelId() : -2;

				fixLoopDetection((ConditionalExpression) exp, loopStartLabel, stack.get(i));

				improveBreakAndContinue((ConditionalExpression) exp, loopStartLabel, nextLabel, (ConditionalExpression) exp);
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
			if (item.getExpression() instanceof UnconditionalJump && ((UnconditionalJump) item.getExpression()).getJumpDestination() == loopStartLabel) {
//				topLevelExp.getThenBranch().addAll(expression.getThenBranch().substack(i, i+1));
				topLevelExp.setElseBranchEnd(((UnconditionalJump) item.getExpression()).getJumpDestination());
			}
		}

		if (((ConditionalExpression) stackItem.getExpression()).getElseBranch() != null) {
			for (int i = 0; i < expression.getElseBranch().getAll().size(); i++) {
				StackItem item = expression.getElseBranch().getAll().get(i);
				fixLoopDetection(topLevelExp, loopStartLabel, item);
				if (item.getExpression() instanceof UnconditionalJump && ((UnconditionalJump) item.getExpression()).getJumpDestination() == loopStartLabel) {
//					topLevelExp.getThenBranch().addAll(expression.getElseBranch().substack(i, i+1));
					topLevelExp.setElseBranchEnd(((UnconditionalJump) item.getExpression()).getJumpDestination());
				}
			}
		}
	}

	private void improveBreakAndContinue(ConditionalExpression exp, int currentLabel, int nextLabel, ConditionalExpression topLevelExpression) {
		List<StackItem> thenBranch;
		List<StackItem> elseBranch;

		if (exp.getThenBranch() != null) {
			thenBranch = exp.getThenBranch().getAll();
			for (StackItem innerExp : thenBranch) {
				if (innerExp.getExpression() instanceof ConditionalExpression) {
					improveBreakAndContinue((ConditionalExpression) innerExp.getExpression(), currentLabel, nextLabel, topLevelExpression);
				}
			}
			addBreak(nextLabel, topLevelExpression, thenBranch);
			addContinue(exp, topLevelExpression, thenBranch);
		}

		if (exp.getElseBranch() != null) {
			elseBranch = exp.getElseBranch().getAll();
			for (StackItem innerExp : elseBranch) {
				if (innerExp.getExpression() instanceof ConditionalExpression) {
					improveBreakAndContinue((ConditionalExpression) innerExp.getExpression(), currentLabel, nextLabel, topLevelExpression);
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
