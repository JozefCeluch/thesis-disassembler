package com.thesis;

import com.thesis.block.MethodBlock;
import com.thesis.block.Statement;
import com.thesis.common.DataType;
import com.thesis.common.Util;
import com.thesis.expression.*;
import com.thesis.expression.TryCatchExpression;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class InstructionTranslator {

	private final MethodNode mMethod;
	private Map<Integer, LocalVariable> mLocalVariables;
	private TryCatchManager mTryCatchManager;
	private MethodBlock mMethodBlock;

	private ExpressionStack mStack;
	private State mState;

	public InstructionTranslator(MethodBlock methodBlock) {
		mState = new State();
		mMethodBlock = methodBlock;
		mStack = new ExpressionStack();
		mState.setStack(mStack);
		mMethod = methodBlock.getMethodNode();
		copyLocalVariables();
		mTryCatchManager = TryCatchManager.newInstance(mMethod.tryCatchBlocks, mStack);
	}

	private void copyLocalVariables() {
		mLocalVariables = new HashMap<>();

		if (mMethod.localVariables.size() > 0) {
			for (Object var : mMethod.localVariables) {
				LocalVariableNode variable = (LocalVariableNode) var;
				mLocalVariables.put(variable.index, new LocalVariable(variable));
			}
		}
		mLocalVariables.putAll(mMethodBlock.getArguments());

	}

	public List<Statement> addCode() {
		System.out.println(" ");
		System.out.println("METHOD: " + mMethod.name);

		mStack.addEnhancer(new LoopEnhancer());
		mState.setCurrentNode(mMethod.instructions.getFirst());
		while (mState.getCurrentNode() != null) {
			pushNodeToStackAsExpression(mState.getCurrentNode(), mStack);
			mState.moveNode();
		}

		mStack.enhance();

		List<Statement> statements = getLocalVariableAssignments();
		StatementCreator sc = new StatementCreator(mStack);
		statements.addAll(sc.getStatements());
		return statements;
	}

	private void pushNodeToStackAsExpression(AbstractInsnNode node, ExpressionStack stack) {
		switch (node.getType()) {
			case AbstractInsnNode.INSN:
				visitInsnNode((InsnNode) node, stack);
				break;
			case AbstractInsnNode.INT_INSN:
				visitIntInsnNode((IntInsnNode) node, stack);
				break;
			case AbstractInsnNode.VAR_INSN:
				visitVarInsnNode((VarInsnNode) node, stack);
				break;
			case AbstractInsnNode.TYPE_INSN:
				visitTypeInsnNode((TypeInsnNode) node, stack);
				break;
			case AbstractInsnNode.FIELD_INSN:
				visitFieldInsnNode((FieldInsnNode) node, stack);
				break;
			case AbstractInsnNode.METHOD_INSN:
				visitMethodInsnNode((MethodInsnNode) node, stack);
				break;
			case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
				visitInvokeDynamicInsnNode((InvokeDynamicInsnNode) node, stack);
				break;
			case AbstractInsnNode.JUMP_INSN:
				visitJumpInsnNode((JumpInsnNode) node, stack);
				break;
			case AbstractInsnNode.LABEL:
				visitLabelNode((LabelNode) node, stack);
				createTryCatchBlocks(stack);
				break;
			case AbstractInsnNode.LDC_INSN:
				visitLdcInsnNode((LdcInsnNode) node, stack);
				break;
			case AbstractInsnNode.IINC_INSN:
				visitIincInsnNode((IincInsnNode) node, stack);
				break;
			case AbstractInsnNode.TABLESWITCH_INSN:
				visitTableSwitchInsnNode((TableSwitchInsnNode) node, stack);
				break;
			case AbstractInsnNode.LOOKUPSWITCH_INSN:
				visitLookupSwitchInsnNode((LookupSwitchInsnNode)node, stack);
				break;
			case AbstractInsnNode.MULTIANEWARRAY_INSN:
				visitMultiANewArrayInsnNode((MultiANewArrayInsnNode)node, stack);
				break;
			case AbstractInsnNode.FRAME:
				visitFrameNode((FrameNode) node, stack);
				break;
			case AbstractInsnNode.LINE:
				visitLineNumberNode((LineNumberNode) node, stack);
				break;
			default:
				printNodeInfo(node, stack);
		}
	}

	private void createTryCatchBlocks(ExpressionStack stack) {
		if (mTryCatchManager.isEmpty()) return;
		List<TryCatchItem> tryCatchItems = mTryCatchManager.getItemsWithStartId(mState.getCurrentLabel());
		if (tryCatchItems.isEmpty()) return;

		TryCatchExpression tryCatchExpression = null;
		for(TryCatchItem item : tryCatchItems) {
			prepareTryCatchItem(item, tryCatchExpression);
			tryCatchExpression = new TryCatchExpression(item);
		}
		stack.push(tryCatchExpression);
	}

	private void prepareTryCatchItem(TryCatchItem tryCatchItem, TryCatchExpression innerTryCatchBlock) {
		if (tryCatchItem.getCatchBlockCount() == tryCatchItem.getHandlerTypes().size()) return;

		// fill try block
		tryCatchItem.setTryStack(mStack.getNew());
		if (innerTryCatchBlock != null) {
			tryCatchItem.getTryStack().push(innerTryCatchBlock);
		}

		while (tryCatchItem.getEndId() != mState.getCurrentLabel()) {
			mState.moveNode();
			pushNodeToStackAsExpression(mState.getCurrentNode(), tryCatchItem.getTryStack());
		}

		// ignore repeated finally blocks
		ExpressionStack repeatedFinallyCalls = mStack.getNew();
		while (!tryCatchItem.hasHandlerLabel(mState.getCurrentLabel())) {
			mState.moveNode();
			pushNodeToStackAsExpression(mState.getCurrentNode(), repeatedFinallyCalls);
		}

		int tryCatchBlockEnd = ConditionalExpression.NO_DESTINATION;
		if (repeatedFinallyCalls.peek() instanceof UnconditionalJump) {
			tryCatchBlockEnd = ((UnconditionalJump) repeatedFinallyCalls.peek()).getJumpDestination();
		}

		// fill catch blocks
		for (int i = 0; i < tryCatchItem.getHandlerCount(); i++) {
			tryCatchItem.addCatchBlock(mState.getCurrentLabel(), mStack.getNew());
			int currentBlockLabel = mState.getCurrentLabel();
			if (tryCatchItem.getHandlerType(currentBlockLabel) == null) {
				tryCatchItem.setHasFinallyBlock(true);
				tryCatchItem.setFinallyBlockStart(currentBlockLabel);
			}
			while (mState.getCurrentLabel() == currentBlockLabel || !(tryCatchItem.hasHandlerLabel(mState.getCurrentLabel())
					|| mTryCatchManager.hasCatchHandlerEnd(mState.getCurrentLabel()) || mState.getCurrentLabel() == tryCatchBlockEnd)) {
				mState.moveNode();
				pushNodeToStackAsExpression(mState.getCurrentNode(), tryCatchItem.getCatchBlock(currentBlockLabel));
			}

			// ignore repeated finally blocks
			while (!(tryCatchItem.hasHandlerLabel(mState.getCurrentLabel()) || mState.getCurrentLabel() == tryCatchBlockEnd)) {
				mState.moveNode();
				pushNodeToStackAsExpression(mState.getCurrentNode(), repeatedFinallyCalls);
			}
		}
	}

	private List<Statement> getLocalVariableAssignments() {
		List<Statement> localVars = mLocalVariables.values().stream()
				.filter(variable -> !variable.isArgument() && !variable.isAdded())
				.map(variable -> new Statement(new VariableDeclarationExpression(variable), 0)) //todo variable line number
				.collect(Collectors.toList());
		List<Statement> result = new ArrayList<>();
		result.addAll(0, localVars);
		return result;
	}

	/**
	 * NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5,
	 * LCONST_0, LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1,
	 * IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
	 * IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE,
	 * POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP,
	 * IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB,
	 * IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM,
	 * FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR,
	 * IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR,
	 * I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S,
	 * LCMP, FCMPL, FCMPG, DCMPL, DCMPG,
	 * IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN,
	 * ARRAYLENGTH, ATHROW, MONITORENTER, or MONITOREXIT.
	 */
	private void visitInsnNode(InsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		int opCode = node.getOpcode();
		if (Util.isBetween(opCode, Opcodes.ACONST_NULL, Opcodes.DCONST_1)) {
			stack.push(new ConstantPrimaryExpression(opCode));
		} else if (Util.isBetween(opCode, Opcodes.IALOAD, Opcodes.SALOAD)) {
			stack.push(new ArrayAccessExpression(opCode));
		} else if (Util.isBetween(opCode, Opcodes.IASTORE, Opcodes.SASTORE)) {
			Expression value = stack.pop();
			Expression index = stack.pop();
			Expression stackTop = stack.peek();
			if (stackTop instanceof ArrayCreationExpression) {
				ArrayCreationExpression arrayExpression = (ArrayCreationExpression) stackTop;
				arrayExpression.addMember(value);
			} else if (stackTop instanceof PrimaryExpression) {
				stack.push(new ArrayAssignmentExpression(opCode, index, value));
			}

		} else if (Util.isBetween(opCode, Opcodes.POP, Opcodes.POP2)) {
//			pop should not remove the expression from the stack //TODO
		} else if (Util.isBetween(opCode, Opcodes.DUP, Opcodes.DUP2_X2)) {
//			stack.push(stack.peek()); //TODO
		} else if (opCode == Opcodes.SWAP) {
			stack.swap();
		} else if (Util.isBetween(opCode, Opcodes.I2L, Opcodes.I2D) || Util.isBetween(opCode, Opcodes.I2B, Opcodes.I2S)) {
			Expression top = stack.peek();
			top.setType(DataType.INT);
			setCorrectCastType(opCode, top);
		} else if (Util.isBetween(opCode, Opcodes.F2L, Opcodes.F2D)) {
			Expression top = stack.peek();
			top.setType(DataType.FLOAT);
			setCorrectCastType(opCode, top);
		} else if (Util.isBetween(opCode, Opcodes.D2L, Opcodes.D2F)) {
			Expression top = stack.peek();
			top.setType(DataType.FLOAT);
			setCorrectCastType(opCode, top);
		} else if (Util.isBetween(opCode, Opcodes.FCMPL, Opcodes.DCMPG) || opCode == Opcodes.LCMP) {
			stack.push(new MultiConditional(opCode, ConditionalExpression.NO_DESTINATION, stack.pop(), stack.pop()));
		} else if (Util.isBetween(opCode, Opcodes.IADD, Opcodes.LXOR)) {
			stack.push(new ArithmeticExpression(opCode));
		} else if (Util.isBetween(opCode, Opcodes.IRETURN, Opcodes.RETURN)) {
			stack.push(new ReturnExpression(opCode));
		} else if (opCode == Opcodes.ARRAYLENGTH) {
			stack.push(new ArrayLengthExpression(opCode));
		} else if (opCode == Opcodes.ATHROW) {
			stack.push(new ThrowExpression(opCode));
		} else if (opCode == Opcodes.MONITORENTER || opCode == Opcodes.MONITOREXIT) {
			stack.push(new MonitorExpression(opCode));
		} else {
			//NOP, do nothing
		}
		//todo to add missing
	}

	private void setCorrectCastType(int opCode, Expression top) {
		switch (opCode) {
			case Opcodes.I2B:
				top.setCastType(DataType.BYTE);
				break;
			case Opcodes.I2C:
				top.setCastType(DataType.CHAR);
				break;
			case Opcodes.I2D:
				top.setCastType(DataType.DOUBLE);
				break;
			case Opcodes.I2F:
				top.setCastType(DataType.FLOAT);
				break;
			case Opcodes.I2L:
				top.setCastType(DataType.LONG);
				break;
			case Opcodes.I2S:
				top.setCastType(DataType.SHORT);
				break;
		}
	}

	//	BIPUSH, SIPUSH or NEWARRAY
	private void visitIntInsnNode(IntInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		int opCode = node.getOpcode();
		switch (opCode) {
			case Opcodes.BIPUSH:
				stack.push(new PrimaryExpression(opCode, node.operand, DataType.BYTE));
				break;
			case Opcodes.SIPUSH:
				stack.push(new PrimaryExpression(opCode, node.operand, DataType.SHORT));
				break;
			case Opcodes.NEWARRAY:
				stack.push(new ArrayCreationExpression(opCode, node.operand));
				break;
		}
	}

	//	ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET
	private void visitVarInsnNode(VarInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		int opCode = node.getOpcode();
		if (Util.isBetween(opCode, Opcodes.ILOAD, Opcodes.ALOAD)) {
			LocalVariable var = mLocalVariables.get(node.var);
			stack.push(new VariablePrimaryExpression(opCode, var));
		}
		if (Util.isBetween(opCode, Opcodes.ISTORE, Opcodes.ASTORE)) {
			if (!mLocalVariables.containsKey(node.var)) {
				mLocalVariables.put(node.var, new LocalVariable("var" + node.var, DataType.UNKNOWN, node.var)); //TODO set type according to the instruction
			}
			LocalVariable localVar = mLocalVariables.get(node.var);
			stack.push(new AssignmentExpression(opCode, new LeftHandSide(opCode, localVar)));
		}
		// RET is deprecated since Java 6
	}

	//	NEW, ANEWARRAY, CHECKCAST or INSTANCEOF
	private void visitTypeInsnNode(TypeInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		int opCode = node.getOpcode();
		if (opCode == Opcodes.NEW) {
			stack.push(new NewExpression(opCode, node.desc));
		}
		if (opCode == Opcodes.INSTANCEOF) {
			stack.push(new InstanceOfExpression(opCode, node.desc));
		}
		if (opCode == Opcodes.ANEWARRAY) {
			stack.push(new ArrayCreationExpression(opCode, node.desc));
		}
	}

	//	GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD
	private void visitFieldInsnNode(FieldInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		int opCode = node.getOpcode();
		if (opCode == Opcodes.PUTFIELD) {
			Expression value = stack.pop();
			if (!(stack.peek() instanceof PrimaryExpression)) { //TODO expects Primary expression on top because the previous was popped, maybe implementing DUP and POP is necessary
				stack.pop();
			}
			PrimaryExpression owner = (PrimaryExpression)stack.pop();
			DataType ownerType = DataType.getTypeFromObject(owner.getValue().toString());
			GlobalVariable field = new GlobalVariable(node.name, DataType.getTypeFromDesc(node.desc), ownerType);
			stack.push(new AssignmentExpression(opCode, new LeftHandSide(opCode,field), value));
		}
		if (opCode == Opcodes.GETFIELD) {
			PrimaryExpression owner = (PrimaryExpression) stack.pop();
			DataType ownerType = DataType.getTypeFromObject(owner.getValue().toString());
			GlobalVariable field = new GlobalVariable(node.name, DataType.getTypeFromDesc(node.desc), ownerType);
			stack.push(new VariablePrimaryExpression(opCode, field));
		}
		if (opCode == Opcodes.GETSTATIC) {
			GlobalVariable staticField = new GlobalVariable(node.name, DataType.getTypeFromDesc(node.desc), DataType.getType(Type.getObjectType(node.owner)));
			stack.push(new VariablePrimaryExpression(opCode, staticField));
		}
		if (opCode == Opcodes.PUTSTATIC) {
			Expression value = stack.pop();
			GlobalVariable field = new GlobalVariable(node.name, DataType.getTypeFromDesc(node.desc), DataType.getTypeFromObject(node.owner));
			stack.push(new AssignmentExpression(opCode, new LeftHandSide(opCode, field), value));
		}
	}

	//	INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE
	private void visitMethodInsnNode(MethodInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		if (node.getOpcode() == Opcodes.INVOKESPECIAL && Util.isConstructor(node.name)) {
			stack.push(new ConstructorInvocationExpression(node.getOpcode(), node.name, node.desc, node.owner, mMethod.name, mMethodBlock.getClassType()));
		} else {
			stack.push(new MethodInvocationExpression(node.getOpcode(), node.name, node.desc, node.owner, mMethod.name));
		}
	}

	//	INVOKEDYNAMIC
	private void visitInvokeDynamicInsnNode(InvokeDynamicInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		stack.push(new LambdaExpression(node.name, node.desc, node.bsm, node.bsmArgs));
	}

	/**
	 * IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IFNULL or IFNONNULL
	 * IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE,
	 * GOTO, JSR (deprecated since Java 6).
	 */
	private void visitJumpInsnNode(JumpInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		ConditionalExpression exp = makeConditionalExpression(node, stack);
		if (exp == null || exp instanceof UnconditionalJump) {
			stack.push(exp);
			return;
		}
		exp.setThenBranch(mStack.getNew());
		if (exp.getStartFrameLocation() == ConditionalExpression.NO_DESTINATION && mState.getFrameLabel() != ConditionalExpression.NO_DESTINATION) {
			exp.setStartFrameLocation(mState.getFrameLabel());
			mState.setFrameLabel(ConditionalExpression.NO_DESTINATION);
		}
		while (!mState.isLabelVisited(exp.getJumpDestination())) {
			mState.moveNode();
			if (isConditionalJump(mState.getCurrentNode())) {
				if (checkLogicGateExpressionIsOnTop(exp)) {
					exp = new LogicGateExpression(exp, (ConditionalExpression) exp.getThenBranch().pop());
				}
			}
			pushNodeToStackAsExpression(mState.getCurrentNode(), exp.getThenBranch());
			if (isEndOfThenBlock(mState.getCurrentNode()) ) {
				int gotoJumpDestination = stack.getLabelId(((JumpInsnNode) mState.getCurrentNode()).label.getLabel());
				exp.setElseBranchEnd(gotoJumpDestination);
				exp.updateThenBranchType();
				if (exp.getJumpDestination() == gotoJumpDestination) {
					break;
					/*break is needed to correctly recognize all cases in switches with Strings because consists of
					* non-standard switch where default case is called after each standard case*/
				}
			}
		}

		if (exp.hasEmptyElseBranch() && !exp.isLoop()) {
			exp.setElseBranch(mStack.getNew());
			while(!mState.isLabelVisited(exp.getElseBranchEnd())) { //mCurrentLabel != exp.getElseBranchEnd()
				mState.moveNode();
				pushNodeToStackAsExpression(mState.getCurrentNode(), exp.getElseBranch());
			}
			exp.updateElseBranchType();
		}

		if (exp.isTernaryExpression()) {
			stack.push(new TernaryExpression(exp));
		} else {
			stack.push(exp);
		}
		stack.setLabel(mState.getCurrentLabel());
	}

	private boolean isEndOfThenBlock(AbstractInsnNode movedNode) {
		return movedNode instanceof JumpInsnNode && movedNode.getOpcode() == Opcodes.GOTO;
	}

	private boolean isConditionalJump(AbstractInsnNode movedNode) {
		return movedNode instanceof JumpInsnNode && movedNode.getOpcode() != Opcodes.GOTO;
	}

	private boolean checkLogicGateExpressionIsOnTop(ConditionalExpression exp) {
		ExpressionStack thenBranchBackup = exp.getThenBranch().duplicate();
		ConditionalExpression innerExp = makeConditionalExpression((JumpInsnNode) mState.getCurrentNode(), exp.getThenBranch());
		exp.getThenBranch().push(innerExp);
		if (exp.containsLogicGateExpression()) {
			mState.moveNode();
			return true;
		} else {
			exp.setThenBranch(thenBranchBackup);
			visitJumpInsnNode((JumpInsnNode) mState.getCurrentNode(), exp.getThenBranch());
			return exp.containsLogicGateExpression();
		}
	}

	private ConditionalExpression makeConditionalExpression(JumpInsnNode node, ExpressionStack stack) {
		ConditionalExpression exp = null;

		int jumpDestination = stack.getLabelId(node.label.getLabel());
		int opCode = node.getOpcode();
		System.out.println("L" + jumpDestination);

		if (Util.isBetween(opCode, Opcodes.IF_ICMPEQ, Opcodes.IF_ACMPNE)) {
			exp = new MultiConditional(opCode, jumpDestination, stack.pop(), stack.pop());
		} else if (Util.isBetween(opCode, Opcodes.IFEQ, Opcodes.IFLE)) {
			Expression stackTop = stack.peek();
			if (stackTop instanceof MultiConditional && !((MultiConditional) stackTop).isJumpDestinationSet()) {
				exp = (MultiConditional) stack.pop();
				exp.setJumpDestination(jumpDestination);
				exp.setOpCode(node.getOpcode());
			} else {
				exp = new SingleConditional(opCode, jumpDestination, stack.pop());
			}
		} else if (Util.isBetween(opCode, Opcodes.IFNULL, Opcodes.IFNONNULL)) {
			exp = new MultiConditional(opCode, jumpDestination, new PrimaryExpression("null", DataType.UNKNOWN), stack.pop());
		} else if (opCode == Opcodes.GOTO) {
			int frameIndex = stack.getExpressionIndexOfFrame(jumpDestination);
			if (frameIndex != -1) {
				exp = new UnconditionalJump(opCode, jumpDestination, stack.substack(frameIndex, stack.size()));
			} else {
				exp = new UnconditionalJump(opCode, jumpDestination);
			}
		}
		if (opCode != Opcodes.GOTO && exp != null && node.getNext() != null && node.getNext() instanceof LabelNode) {
			exp.setThenBranchStart(stack.getLabelId(((LabelNode) node.getNext()).getLabel()));
		}
		return exp;
	}

	private void visitLabelNode(LabelNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		mState.setCurrentLabel(stack.getLabelId(node.getLabel()));
		stack.setLabel(mState.getCurrentLabel());
		mState.addLabelToVisited(mState.getCurrentLabel());
		System.out.println("LABEL: " + "L" + mState.getCurrentLabel());
	}

	// LDC
	private void visitLdcInsnNode(LdcInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		DataType type;
		Object constant = node.cst;
		if (constant instanceof Integer) {
			type = DataType.INT;
		} else if (constant instanceof Float) {
			type = DataType.FLOAT;
		} else if (constant instanceof Double) {
			type = DataType.DOUBLE;
		} else if (constant instanceof Long) {
			type = DataType.LONG;
		} else if (constant instanceof String) {
			type = DataType.getTypeFromObject("java.lang.String");
		} else {
			constant = DataType.getType((Type) node.cst); //todo think is this correct?
			type = DataType.getTypeFromObject("java.lang.Class");
		}
		stack.push(new PrimaryExpression(node.getOpcode(), constant, type));
	}

	private void visitIincInsnNode(IincInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		LocalVariable variable = mLocalVariables.get(node.var);
		if (node.getPrevious() != null && node.getPrevious().getOpcode() == Opcodes.ILOAD) {
			stack.push(new UnaryExpression(node.getOpcode(), variable, DataType.INT, UnaryExpression.OpPosition.POSTFIX));
			return;
		}
		if (node.getNext() != null && node.getNext().getOpcode() == Opcodes.ILOAD) {
			stack.push(new UnaryExpression(node.getOpcode(), variable, DataType.INT, UnaryExpression.OpPosition.PREFIX));
			return;
		}
		stack.push(new AssignmentExpression(node.getOpcode(), new LeftHandSide(node.getOpcode(), variable), new PrimaryExpression(node.getOpcode(), node.incr, DataType.INT)));
	}

	//	TABLESWITCH
	private void visitTableSwitchInsnNode(TableSwitchInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
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

	private void updateSwitchWithCases(SwitchExpression switchExp, int defaultLabel, Map<Integer, String> labelCaseMap) {
		int switchEndLabel = -1;
		ExpressionStack caseStack = null;
		SwitchExpression.CaseExpression caseExpression = null;

		while (mState.getCurrentLabel() != switchEndLabel && mState.getCurrentNode() != null) {
			if (caseStack == null) {
				caseStack = mStack.getNew();
			}

			pushNodeToStackAsExpression(mState.getCurrentNode(), caseStack);
			mState.moveNode();

			if (labelCaseMap.containsKey(mState.getCurrentLabel()) && caseExpression == null) {
				caseExpression = new SwitchExpression.CaseExpression(labelCaseMap.get(mState.getCurrentLabel()), mState.getCurrentLabel(), defaultLabel, caseStack);
				switchExp.addCase(caseExpression);
			}

			if (caseExpression != null && labelCaseMap.containsKey(mState.getCurrentLabel()) && caseExpression.getLabel() != mState.getCurrentLabel()) {
				if (caseStack.peek() instanceof UnconditionalJump) {
					UnconditionalJump jump = (UnconditionalJump) caseStack.pop();
					switchEndLabel = jump.getJumpDestination();
					caseStack.push(new BreakExpression(jump));
				}
				caseStack = null;
				caseExpression = null;
			}

		}
	}

	// LOOKUPSWITCH
	private void visitLookupSwitchInsnNode(LookupSwitchInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
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

	//	MULTIANEWARRAY
	private void visitMultiANewArrayInsnNode(MultiANewArrayInsnNode node, ExpressionStack stack) {
		printNodeInfo(node, stack);
		stack.push(new ArrayCreationExpression(node.getOpcode(), node.desc, node.dims));
	}

	private void visitFrameNode(FrameNode node, ExpressionStack stack) {
		System.out.println("FRAME:");
		printNodeInfo(node, stack);
		System.out.println("local: " + Arrays.deepToString(node.local.toArray()));
		System.out.println("stack: " + Arrays.deepToString(node.stack.toArray()));

		mState.setFrameLabel(mState.getCurrentLabel());
		stack.addFrame(mState.getCurrentLabel());
	}

	private void visitLineNumberNode(LineNumberNode node, ExpressionStack stack) {
		System.out.println("LINE: " + node.line);
		printNodeInfo(node, stack);
		stack.setLineNumber(node.line);
//		mState.setCurrentLine(node.line);
	}

	private void printNodeInfo(AbstractInsnNode node, ExpressionStack stack) {
		String opCode = Util.getOpcodeString(node.getOpcode());
		if (opCode.isEmpty()) return;
		String fields = "";
		for (Field field : node.getClass().getFields()) {
			if (!(field.getName().contains("INSN") || field.getName().contains("LABEL") || field.getName().contains("FRAME") || field.getName().contains("LINE"))) {
				try {
					fields += field.getName() + " = " + field.get(node);
					fields += "; ";
				} catch (IllegalAccessException e) {
					System.out.println(field.getName() + " is inaccessible");
				}
			}

		}
//		System.out.println("STACK: " + mStack.size());
		String result = "code: " + opCode + " " + fields;
		result += "\nCURRENT LABEL: " + mState.getCurrentLabel() + ",  STACK: " + stack.getLabel();
		System.out.println(result);
	}

	private static class State {
		private int mFrameLabel = ConditionalExpression.NO_DESTINATION;
		private int mCurrentLabel;
		private int mCurrentLine;
		private List<Integer> mVisitedLabels;
		private AbstractInsnNode mCurrentNode;

		private ExpressionStack mStack;
		private Stack<ExpressionStack> mActiveStacks;

		public State() {
			mVisitedLabels = new ArrayList<>();
			mActiveStacks = new Stack<>();
		}

		public void setStack(ExpressionStack stack) {
			if (!mActiveStacks.isEmpty()) {
				throw new RuntimeException("there is a stack already");
			}
			mStack = stack;
			mActiveStacks.push(mStack);
		}

		public int getFrameLabel() {
			return mFrameLabel;
		}

		public void setFrameLabel(int frameLabel) {
			mFrameLabel = frameLabel;
		}

		public int getCurrentLabel() {
			return mCurrentLabel;
		}

		public void setCurrentLabel(int currentLabel) {
			mCurrentLabel = currentLabel;
		}

		public boolean isLabelVisited(int label) {
			return mVisitedLabels.contains(label);
		}

		public void addLabelToVisited(int label) {
			mVisitedLabels.add(label);
		}

		public AbstractInsnNode getCurrentNode() {
			return mCurrentNode;
		}

		public void setCurrentNode(AbstractInsnNode currentNode) {
			mCurrentNode = currentNode;
		}

		public AbstractInsnNode moveNode() {
			if (mCurrentNode != null) {
				mCurrentNode = mCurrentNode.getNext();
			}
			return mCurrentNode;
		}

		public void setCurrentLine(int currentLine) {
			mCurrentLine = currentLine;
//			getActiveStack().setLineNumber(mCurrentLine);
		}
	}
}
