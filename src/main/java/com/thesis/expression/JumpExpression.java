package com.thesis.expression;

import com.thesis.common.DataType;
import com.thesis.translator.ExpressionStack;
import org.objectweb.asm.Opcodes;

/**
 * Abstract general expression that provides the base for all possible jump expressions
 */
public abstract class JumpExpression extends Expression {

	/**
	 * Destination is not set
	 */
	public static final int NO_DESTINATION = -1;

	/**
	 * Destination of the expression jump (label ID)
	 */
	protected int mJumpDestination = NO_DESTINATION;

	/**
	 * End of else branch (label ID)
	 */
	protected int mElseBranchEnd = NO_DESTINATION;

	/**
	 * End of then branch (label ID)
	 */
	protected int mThenBranchStart = NO_DESTINATION;

	/**
	 * Location of the frame at the beginning of the loop (if any)
	 */
	protected int mStartFrameLocation = NO_DESTINATION;
	protected ExpressionStack thenBranch;
	protected ExpressionStack elseBranch;
	protected Operator mOperator;
	protected LoopType mLoopType;

	public JumpExpression(int opCode, int jumpDestination) {
		super(opCode);
		mType = DataType.BOOLEAN;
		mJumpDestination = jumpDestination;
		mOperator = makeOperator(opCode).neg();
		mLoopType = LoopType.NONE;
	}

	@Override
	public void setOpCode(int instruction) {
		super.setOpCode(instruction);
		mOperator = makeOperator(instruction).neg();
	}

	public int getJumpDestination() {
		return mJumpDestination;
	}

	public void setJumpDestination(int jumpDestination) {
		mJumpDestination = jumpDestination;
	}

	public int getElseBranchEnd() {
		return mElseBranchEnd;
	}

	public void setElseBranchEnd(int elseBranchEnd) {
		mElseBranchEnd = elseBranchEnd;
	}

	public int getThenBranchStart() {
		return mThenBranchStart;
	}

	public void setThenBranchStart(int thenBranchStart) {
		mThenBranchStart = thenBranchStart;
	}

	public int getStartFrameLocation() {
		return mStartFrameLocation;
	}

	public void setStartFrameLocation(int startFrameLocation) {
		mStartFrameLocation = startFrameLocation;
	}

	public ExpressionStack getThenBranch() {
		return thenBranch;
	}

	public ExpressionStack getElseBranch() {
		return elseBranch;
	}

	public void setElseBranch(ExpressionStack elseBranch) {
		this.elseBranch = elseBranch;
	}

	public void setThenBranch(ExpressionStack thenBranch) {
		this.thenBranch = thenBranch;
	}

	public LoopType getLoopType() {
		if (LoopType.NONE.equals(mLoopType) && isLoop()) {
			mLoopType = LoopType.WHILE;
		}
		return mLoopType;
	}

	public void setLoopType(LoopType loopType) {
		mLoopType = loopType;
	}

	public boolean hasEmptyElseBranch() {
		return mElseBranchEnd != NO_DESTINATION && mElseBranchEnd != mJumpDestination && (elseBranch == null || elseBranch.isEmpty());
	}

	public boolean isJumpDestinationSet() {
		return mJumpDestination != NO_DESTINATION;
	}

	/**
	 * Helper method to test if the expression is a loop
	 *
	 * based on the jump locations
	 * @return true in case of a loop
	 */
	public boolean isLoop() {
		return mStartFrameLocation != NO_DESTINATION && ((mElseBranchEnd != NO_DESTINATION && mElseBranchEnd == mStartFrameLocation)
				|| (mJumpDestination != NO_DESTINATION && mJumpDestination == mStartFrameLocation));
	}

	public void negate() {
		mOperator = mOperator.neg();
	}

	/**
	 * Updates the type of then branch in case of ternary expression
	 */
	public void updateThenBranchType() {
		if (thenBranch.size() == 2) {
			Expression expression = thenBranch.get(0);
			if (expression instanceof PrimaryExpression &&
					(((PrimaryExpression) expression).getValue().equals(1) || ((PrimaryExpression) expression).getValue().equals(0))) {
				expression.setType(DataType.BOOLEAN);
			}
		}
	}

	/**
	 * Updates the type of else branch in case of ternary expression
	 */
	public void updateElseBranchType() {
		if (elseBranch.size() == 1) {
			Expression expression = elseBranch.get(0);
			if (DataType.BOOLEAN.equals(thenBranch.get(0).getType())) {
				expression.setType(DataType.BOOLEAN);
			}
		}
	}

	/**
	 * Test if the expression is a ternary expression
	 * @return true in case of a ternary expression
	 */
	public boolean isTernaryExpression() {
		if (thenBranch == null || elseBranch == null || thenBranch.size() != 2 || elseBranch.size() != 1) return false;

		Expression thenExp = thenBranch.get(0);
		Expression elseExp = elseBranch.get(0);
		return ((thenExp instanceof PrimaryExpression && !(DataType.BOOLEAN.equals(thenExp.getType())))
					|| (thenExp instanceof MethodInvocationExpression && !DataType.VOID.equals(thenExp.getType())))
				&& ((elseExp instanceof PrimaryExpression && !(DataType.BOOLEAN.equals(thenExp.getType())))
					|| (elseExp instanceof MethodInvocationExpression && !DataType.VOID.equals(elseExp.getType()))
					|| elseExp instanceof JumpExpression);
	}

	/**
	 * Test if the expression contains logic gate (short-circuit AND / OR)
	 * @return true in case it is a logic gate expression
	 */
	public boolean containsLogicGateExpression() {
		if (thenBranch.isEmpty()) return false;
		Expression branchTop = thenBranch.get(0);
		return branchTop != null && branchTop instanceof JumpExpression
				&& (mJumpDestination == ((JumpExpression) branchTop).getJumpDestination()
				|| mJumpDestination == ((JumpExpression) branchTop).getThenBranchStart());
	}

	@Override
	public DataType getType() {
		return mType;
	}

	private static Operator makeOperator(int opcode) {
		if (opcode == Opcodes.IFEQ || opcode == Opcodes.IF_ACMPEQ || opcode == Opcodes.IF_ICMPEQ || opcode == Opcodes.IFNULL) {
			return Operator.EQUAL;
		}
		if (opcode == Opcodes.IFNE || opcode == Opcodes.IF_ACMPNE || opcode == Opcodes.IF_ICMPNE || opcode == Opcodes.IFNONNULL){
			return Operator.NOT_EQUAL;
		}
		if (opcode == Opcodes.IFGE || opcode == Opcodes.IF_ICMPGE){
			return Operator.GREATER_EQUAL;
		}
		if (opcode == Opcodes.IFGT || opcode == Opcodes.IF_ICMPGT){
			return Operator.GREATER_THAN;
		}
		if (opcode == Opcodes.IFLE || opcode == Opcodes.IF_ICMPLE){
			return Operator.LESS_EQUAL;
		}
		if (opcode == Opcodes.IFLT || opcode == Opcodes.IF_ICMPLT){
			return Operator.LESS_THAN;
		}

		return Operator.ERR;
	}

	/**
	 * Type of the loop
	 */
	public enum LoopType {
		NONE, WHILE, FOR, DO_WHILE;
	}
}
