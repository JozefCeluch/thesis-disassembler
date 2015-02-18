package com.thesis;

import com.thesis.expression.ExpressionStack;
import com.thesis.expression.StackItem;

import java.util.Stack;

public interface StackEnhancer {

	void enhance(ExpressionStack expressionStack, Stack<StackItem> stack);
}
