package com.thesis.translator;

import com.thesis.expression.stack.ExpressionStack;
import com.thesis.expression.stack.StackItem;

import java.util.Stack;

public interface StackEnhancer {

	void enhance(ExpressionStack expressionStack, Stack<StackItem> stack);
}
