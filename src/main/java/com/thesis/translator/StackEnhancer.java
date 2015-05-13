package com.thesis.translator;

import java.util.Stack;

public interface StackEnhancer {

	void enhance(ExpressionStack expressionStack, Stack<ExpressionStack.Item> stack);
}
