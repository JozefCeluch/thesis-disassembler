package com.thesis.expression;

import com.thesis.common.DataType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

public abstract class ConditionalExpression extends Expression {

	public static final int NO_DESTINATION = -1;

	protected int mJumpDestination = NO_DESTINATION;
	protected int mElseBranchEnd = NO_DESTINATION;
	protected int mThenBranchStart = NO_DESTINATION;
	protected int mStartFrameLocation = NO_DESTINATION;
	protected ExpressionStack thenBranch;
	protected ExpressionStack elseBranch;
	protected Operand mOperand;

	public ConditionalExpression(AbstractInsnNode instruction, int jumpDestination) {
		super(instruction);
		mType = DataType.BOOLEAN;
		mJumpDestination = jumpDestination;
		thenBranch = new ExpressionStack();
		elseBranch = new ExpressionStack();
		mOperand = makeOperand(instruction).neg();
	}

	public ConditionalExpression(int jumpDestination) {
		this(null, jumpDestination);
	}

	@Override
	public void setInstruction(AbstractInsnNode instruction) {
		super.setInstruction(instruction);
		mOperand = makeOperand(instruction).neg();
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

	public void setThenBranch(ExpressionStack thenBranch) {
		this.thenBranch = thenBranch;
	}

	public boolean hasEmptyElseBranch() {
		return mElseBranchEnd != NO_DESTINATION && mElseBranchEnd != mJumpDestination && elseBranch.isEmpty();
	}

	public boolean isJumpDestinationSet() {
		return mJumpDestination != NO_DESTINATION;
	}

	public boolean isLoop() {
		return mElseBranchEnd != NO_DESTINATION && mStartFrameLocation != NO_DESTINATION && mElseBranchEnd == mStartFrameLocation;
	}

	public void negate() {
		mOperand = mOperand.neg();
	}

	public void updateThenBranchType() {
		if (thenBranch.size() == 2) {
			Expression expression = thenBranch.get(0);
			if (expression instanceof PrimaryExpression &&
					(((PrimaryExpression) expression).getValue().equals(1) || ((PrimaryExpression) expression).getValue().equals(0))) {
				expression.setType(DataType.BOOLEAN);
			}
		}
	}

	public void updateElseBranchType() {
		if (elseBranch.size() == 1) {
			Expression expression = elseBranch.get(0);
			if (DataType.BOOLEAN.equals(thenBranch.get(0).getType())) {
				expression.setType(DataType.BOOLEAN);
			}
		}
	}

	public boolean isTernaryExpression() {
		if (thenBranch.size() != 2 || elseBranch.size() != 1) return false;

		Expression thenExp = thenBranch.get(0);
		Expression elseExp = elseBranch.get(0);
		return ((thenExp instanceof PrimaryExpression && !(DataType.BOOLEAN.equals(thenExp.getType())))
					|| (thenExp instanceof MethodInvocationExpression && !DataType.VOID.equals(thenExp.getType())))
				&& ((elseExp instanceof PrimaryExpression && !(DataType.BOOLEAN.equals(thenExp.getType())))
					|| (elseExp instanceof MethodInvocationExpression && !DataType.VOID.equals(elseExp.getType()))
					|| elseExp instanceof ConditionalExpression);
	}

	public boolean containsLogicGateExpression() {
		if (thenBranch.isEmpty()) return false;
		Expression branchTop = thenBranch.get(0);
		return branchTop != null && branchTop instanceof ConditionalExpression
				&& (mJumpDestination == ((ConditionalExpression) branchTop).getJumpDestination()
				|| mJumpDestination == ((ConditionalExpression) branchTop).getThenBranchStart());
	}

	@Override
	public DataType getType() {
		return mType;
	}

	private static Operand makeOperand(AbstractInsnNode instruction) {
		if (instruction == null) {
			return Operand.ERR;
		}
		int opcode = instruction.getOpcode();
		if (opcode == Opcodes.IFEQ || opcode == Opcodes.IF_ACMPEQ || opcode == Opcodes.IF_ICMPEQ || opcode == Opcodes.IFNULL) {
			return Operand.EQUAL;
		}
		if (opcode == Opcodes.IFNE || opcode == Opcodes.IF_ACMPNE || opcode == Opcodes.IF_ICMPNE || opcode == Opcodes.IFNONNULL){
			return Operand.NOT_EQUAL;
		}
		if (opcode == Opcodes.IFGE || opcode == Opcodes.IF_ICMPGE){
			return Operand.GREATER_EQUAL;
		}
		if (opcode == Opcodes.IFGT || opcode == Opcodes.IF_ICMPGT){
			return Operand.GREATER_THAN;
		}
		if (opcode == Opcodes.IFLE || opcode == Opcodes.IF_ICMPLE){
			return Operand.LESS_EQUAL;
		}
		if (opcode == Opcodes.IFLT || opcode == Opcodes.IF_ICMPLT){
			return Operand.LESS_THAN;
		}

		return Operand.ERR;
	}
}
