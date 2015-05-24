package com.thesis.translator;

import java.util.Stack;

/**
 * Interface that can be used to enhance {@link ExpressionStack} after the decompilation process finished
 */
public interface StackEnhancer {

	/**
	 * This method allows to change the stack in any way possile
	 * @param expressionStack the expression stack that is being enhanced
	 * @param stack the inner stack in the ExpressionStack
	 */
	void enhance(ExpressionStack expressionStack, Stack<ExpressionStack.Item> stack);
}
