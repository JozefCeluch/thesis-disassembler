package com.thesis;

import com.thesis.block.Block;
import com.thesis.block.Statement;
import com.thesis.common.DataType;
import com.thesis.common.Util;
import com.thesis.expression.*;
import com.thesis.expression.TryCatchExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class InstructionTranslator {

	private final MethodNode mMethod;
	private ExpressionStack mStack;
	private List<Block> mStatements;
	private Map<Integer, LocalVariable> mLocalVariables;
	private Label mLabel;
	private int mCurrentLabel;
	private TryCatchManager mTryCatchManager;
	private int mFrameLabel = ConditionalExpression.NO_DESTINATION;
	private List<Integer> mVisitedLabels;

	public InstructionTranslator(MethodNode method, List<Block> statements, Map<Integer, LocalVariable> arguments) {
		mStatements = statements;
		mStack = new ExpressionStack(new HashMap<>());
		mMethod = method;
		mLocalVariables = new HashMap<>();
		copyLocalVariables();
		mLocalVariables.putAll(arguments);
		mTryCatchManager = TryCatchManager.newInstance(method.tryCatchBlocks, mStack);
		mVisitedLabels = new ArrayList<>();
	}

	private void copyLocalVariables() {
		if (mMethod.localVariables.size() > 0) {
			for (Object var : mMethod.localVariables) {
				LocalVariableNode variable = (LocalVariableNode) var;
				mLocalVariables.put(variable.index, new LocalVariable(variable));
			}
		}
	}

	public void addCode() {
		System.out.println(" ");
		System.out.println("METHOD: " + mMethod.name);
		AbstractInsnNode node = mMethod.instructions.getFirst();
		while (node != null) {
			node = pushNodeToStackAsExpression(node, mStack);
			if (node != null) node = node.getNext();
		}
		addLocalVariablesAssignments();

		mStack.addEnhancer(new LoopEnhancer());

		mStack.enhance();

		StatementCreator sc = new StatementCreator(mStack);
		mStatements.addAll(sc.getStatements());
	}

	private AbstractInsnNode pushNodeToStackAsExpression(AbstractInsnNode node, ExpressionStack stack) {
		if (mLabel != null) stack.addLabel(mLabel);

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
				node = visitJumpInsnNode((JumpInsnNode) node, stack);
				break;
			case AbstractInsnNode.LABEL:
				visitLabelNode((LabelNode) node, stack);
				node = createTryCatchBlocks(node, stack);
				break;
			case AbstractInsnNode.LDC_INSN:
				visitLdcInsnNode((LdcInsnNode) node, stack);
				break;
			case AbstractInsnNode.IINC_INSN:
				visitIincInsnNode((IincInsnNode) node, stack);
				break;
			case AbstractInsnNode.TABLESWITCH_INSN:
				node = visitTableSwitchInsnNode((TableSwitchInsnNode) node, stack);
				break;
			case AbstractInsnNode.LOOKUPSWITCH_INSN:
				node = visitLookupSwitchInsnNode((LookupSwitchInsnNode)node, stack);
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
				printNodeInfo(node);
		}
		return node;
	}

	private AbstractInsnNode createTryCatchBlocks(AbstractInsnNode node, ExpressionStack stack) {
		if (mTryCatchManager.isEmpty()) return node;
		List<TryCatchItem> tryCatchItems = mTryCatchManager.getItemsWithStartId(mCurrentLabel);
		if (tryCatchItems.isEmpty()) return node;

		AbstractInsnNode movedNode = node;
		TryCatchExpression previousExpression = null;
		for(TryCatchItem item : tryCatchItems) {
			item.setTryStack(mStack.getNew());
			if (previousExpression != null) {
				item.getTryStack().push(previousExpression);
			}
			movedNode = processTryCatchBlock(movedNode, item);
			previousExpression = new TryCatchExpression(item);
		}
		stack.push(previousExpression);
		return movedNode;
	}

	private AbstractInsnNode processTryCatchBlock(AbstractInsnNode node, TryCatchItem tryCatchItem) {
		AbstractInsnNode movedNode = node;
		if (tryCatchItem.getCatchBlockCount() == tryCatchItem.getHandlerTypes().size()) return node;

		// fill try block
		while (tryCatchItem.getEndId() != mCurrentLabel) {
			movedNode = movedNode.getNext();
			movedNode = pushNodeToStackAsExpression(movedNode, tryCatchItem.getTryStack());
		}

		// ignore repeated finally blocks
		ExpressionStack repeatedFinallyCalls = mStack.getNew();
		while (!tryCatchItem.hasHandlerLabel(mCurrentLabel)) {
			movedNode = movedNode.getNext();
			movedNode = pushNodeToStackAsExpression(movedNode, repeatedFinallyCalls);
		}
		int tryCatchBlockEnd = ConditionalExpression.NO_DESTINATION;
		if (repeatedFinallyCalls.peek() instanceof UnconditionalJump) {
			tryCatchBlockEnd = ((UnconditionalJump) repeatedFinallyCalls.peek()).getJumpDestination();
		}

		// fill catch blocks
		for (int i = 0; i < tryCatchItem.getHandlerCount(); i++) {
			tryCatchItem.addCatchBlock(mCurrentLabel, mStack.getNew());
			int currentBlockLabel = mCurrentLabel;
			if (tryCatchItem.getHandlerType(currentBlockLabel) == null) {
				tryCatchItem.setHasFinallyBlock(true);
				tryCatchItem.setFinallyBlockStart(currentBlockLabel);
			}
			while (mCurrentLabel == currentBlockLabel || !(tryCatchItem.hasHandlerLabel(mCurrentLabel)
					|| mTryCatchManager.hasCatchHandlerEnd(mCurrentLabel) || mCurrentLabel == tryCatchBlockEnd)) {
				movedNode = movedNode.getNext();
				movedNode = pushNodeToStackAsExpression(movedNode, tryCatchItem.getCatchBlock(currentBlockLabel));
			}

			// ignore repeated finally blocks
			while (!(tryCatchItem.hasHandlerLabel(mCurrentLabel) || mCurrentLabel == tryCatchBlockEnd)) {
				movedNode = movedNode.getNext();
				movedNode = pushNodeToStackAsExpression(movedNode, repeatedFinallyCalls);
			}
		}

		return movedNode;
	}

	private void addLocalVariablesAssignments() {
		List<Block> localVars = mLocalVariables.values().stream()
				.filter(variable -> !variable.isArgument() && !variable.isAdded())
				.map(variable -> new Statement(new VariableDeclarationExpression(variable), 0)) //todo variable line number
				.collect(Collectors.toList());
		mStatements.addAll(0, localVars);
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
		printNodeInfo(node);
		int opCode = node.getOpcode();
		if (isBetween(opCode, Opcodes.ACONST_NULL, Opcodes.DCONST_1)) {
			stack.push(new PrimaryExpression(node));
		} else if (isBetween(opCode, Opcodes.IALOAD, Opcodes.SALOAD)) {
			stack.push(new ArrayAccessExpression(node));
		} else if (isBetween(opCode, Opcodes.IASTORE, Opcodes.SASTORE)) {
			Expression value = stack.pop();
			Expression index = stack.pop();
			Expression stackTop = stack.peek();
			if (stackTop instanceof ArrayCreationExpression) {
				ArrayCreationExpression arrayExpression = (ArrayCreationExpression) stackTop;
				arrayExpression.addMember(value);
			} else if (stackTop instanceof PrimaryExpression) {
				stack.push(new ArrayAssignmentExpression(node, index, value));
			}

		} else if (isBetween(opCode, Opcodes.POP, Opcodes.POP2)) {
//			pop should not remove the expression from the stack //TODO
		} else if (isBetween(opCode, Opcodes.DUP, Opcodes.DUP2_X2)) {
//			stack.push(stack.peek()); //TODO
		} else if (opCode == Opcodes.SWAP) {
			stack.swap();
		} else if (isBetween(opCode, Opcodes.I2L, Opcodes.I2D) || isBetween(opCode, Opcodes.I2B, Opcodes.I2S)) {
			Expression top = stack.peek();
			top.setType(DataType.INT);
			setCorrectCastType(opCode, top);
		} else if (isBetween(opCode, Opcodes.F2L, Opcodes.F2D)) {
			Expression top = stack.peek();
			top.setType(DataType.FLOAT);
			setCorrectCastType(opCode, top);
		} else if (isBetween(opCode, Opcodes.D2L, Opcodes.D2F)) {
			Expression top = stack.peek();
			top.setType(DataType.FLOAT);
			setCorrectCastType(opCode, top);
		} else if (isBetween(opCode, Opcodes.FCMPL, Opcodes.DCMPG) || opCode == Opcodes.LCMP) {
			stack.push(new MultiConditional(node, ConditionalExpression.NO_DESTINATION, stack.pop(), stack.pop(), mStack.getNew()));
		} else if (isBetween(opCode, Opcodes.IADD, Opcodes.LXOR)) {
			stack.push(new ArithmeticExpression(node));
		} else if (isBetween(opCode, Opcodes.IRETURN, Opcodes.RETURN)) {
			stack.push(new ReturnExpression(node));
		} else if (opCode == Opcodes.ARRAYLENGTH) {
			stack.push(new ArrayLengthExpression(node));
		} else if (opCode == Opcodes.ATHROW) {
			stack.push(new ThrowExpression(node));
		} else if (opCode == Opcodes.MONITORENTER || opCode == Opcodes.MONITOREXIT) {
			stack.push(new MonitorExpression(node));
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
		printNodeInfo(node);
		int opCode = node.getOpcode();
		switch (opCode) {
			case Opcodes.BIPUSH:
				stack.push(new PrimaryExpression(node, node.operand, DataType.BYTE));
				break;
			case Opcodes.SIPUSH:
				stack.push(new PrimaryExpression(node, node.operand, DataType.SHORT));
				break;
			case Opcodes.NEWARRAY:
				stack.push(new ArrayCreationExpression(node));
				break;
		}
	}

	//	ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET
	private void visitVarInsnNode(VarInsnNode node, ExpressionStack stack) {
		printNodeInfo(node);
		int opCode = node.getOpcode();
		if (isBetween(opCode, Opcodes.ILOAD, Opcodes.ALOAD)) {
			LocalVariable var = mLocalVariables.get(node.var);
			stack.push(new PrimaryExpression(node, var, var.getType()));
		}
		if (isBetween(opCode, Opcodes.ISTORE, Opcodes.ASTORE)) {
			if (!mLocalVariables.containsKey(node.var)) {
				mLocalVariables.put(node.var, new LocalVariable("var" + node.var, DataType.UNKNOWN, node.var)); //TODO set type according to the instruction
			}
			LocalVariable localVar = mLocalVariables.get(node.var);
			stack.push(new AssignmentExpression(node, new LeftHandSide(node, localVar)));
		}
		// RET is deprecated since Java 6
	}

	//	NEW, ANEWARRAY, CHECKCAST or INSTANCEOF
	private void visitTypeInsnNode(TypeInsnNode node, ExpressionStack stack) {
		printNodeInfo(node);
		int opCode = node.getOpcode();
		if (opCode == Opcodes.NEW) {
			stack.push(new NewExpression(node, DataType.getType(Util.removeOuterClasses(node.desc)))); //TODO probably not remove
		}
		if (opCode == Opcodes.INSTANCEOF) {
			stack.push(new InstanceOfExpression(node));
		}
		if (opCode == Opcodes.ANEWARRAY) {
			stack.push(new ArrayCreationExpression(node));
		}
	}

	//	GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD
	private void visitFieldInsnNode(FieldInsnNode node, ExpressionStack stack) {
		printNodeInfo(node);
		int opCode = node.getOpcode();
		if (opCode == Opcodes.PUTFIELD) {
			Expression value = stack.pop();
			PrimaryExpression owner = (PrimaryExpression)stack.pop();
			DataType ownerType = DataType.getType(owner.getValue().toString());
			GlobalVariable field = new GlobalVariable(node.name, Util.getType(node.desc), ownerType);
			stack.push(new AssignmentExpression(node, new LeftHandSide(node,field), value));
		}
		if (opCode == Opcodes.GETFIELD) {
			PrimaryExpression owner = (PrimaryExpression) stack.pop();
			DataType ownerType = DataType.getType(owner.getValue().toString());
			GlobalVariable field = new GlobalVariable(node.name, Util.getType(node.desc), ownerType);
			stack.push(new PrimaryExpression(node, field,Util.getType(node.desc)));
		}
		if (opCode == Opcodes.GETSTATIC) {
			stack.push(new PrimaryExpression(node, new GlobalVariable(node.name, Util.getType(node.desc), DataType.getType(Util.javaObjectName(node.owner))), Util.getType(node.desc)));
		}
		if (opCode == Opcodes.PUTSTATIC) {
			Expression value = stack.pop();
			GlobalVariable field = new GlobalVariable(node.name, Util.getType(node.desc), DataType.getType(node.owner));
			stack.push(new AssignmentExpression(node, new LeftHandSide(node, field), value));
		}
	}

	//	INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE
	private void visitMethodInsnNode(MethodInsnNode node, ExpressionStack stack) {
		printNodeInfo(node);
		if (node.getOpcode() == Opcodes.INVOKESPECIAL && Util.isConstructor(node.name)) {
			stack.push(new ConstructorInvocationExpression(node, mMethod.name));
		} else {
			stack.push(new MethodInvocationExpression(node, mMethod.name));
		}
	}

	//	INVOKEDYNAMIC
	private void visitInvokeDynamicInsnNode(InvokeDynamicInsnNode node, ExpressionStack stack) {
		printNodeInfo(node);
	}

	/**
	 * IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IFNULL or IFNONNULL
	 * IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE,
	 * GOTO, JSR (deprecated since Java 6).
	 */
	private AbstractInsnNode visitJumpInsnNode(JumpInsnNode node, ExpressionStack stack) {
		printNodeInfo(node);
		ConditionalExpression exp = makeConditionalExpression(node, stack);
		if (exp == null || exp.getThenBranch() == null) {
			stack.push(exp);
			return node;
		}

		if (exp.getStartFrameLocation() == ConditionalExpression.NO_DESTINATION && mFrameLabel != ConditionalExpression.NO_DESTINATION) {
			exp.setStartFrameLocation(mFrameLabel);
			mFrameLabel = ConditionalExpression.NO_DESTINATION;
		}
		AbstractInsnNode movedNode = node;
		while (!mVisitedLabels.contains(exp.getJumpDestination())) {
			movedNode = movedNode.getNext();
			if (isConditionalJump(movedNode)) {
				movedNode = checkLogicGateExpressionIsOnTop(exp, movedNode);
				if (exp.containsLogicGateExpression()) {
					exp = new LogicGateExpression(exp, (ConditionalExpression) exp.getThenBranch().pop());
				}
			}
			movedNode = pushNodeToStackAsExpression(movedNode, exp.getThenBranch());
			if (isEndOfThenBlock(movedNode) ) {
				exp.setElseBranchEnd(stack.getLabelId(((JumpInsnNode) movedNode).label.getLabel()));
				exp.updateThenBranchType();
			}
		}

		if (exp.hasEmptyElseBranch() && !exp.isLoop()) {
			exp.setElseBranch(mStack.getNew());
			while(!mVisitedLabels.contains(exp.getElseBranchEnd())){ //mCurrentLabel != exp.getElseBranchEnd()
				movedNode = movedNode.getNext();
				movedNode = pushNodeToStackAsExpression(movedNode, exp.getElseBranch());
			}
			exp.updateElseBranchType();
		}

		if (exp.isTernaryExpression()) {
			stack.push(new TernaryExpression(exp));
		} else {
			stack.push(exp);
		}
		return movedNode;
	}

	private boolean isEndOfThenBlock(AbstractInsnNode movedNode) {
		return movedNode instanceof JumpInsnNode && movedNode.getOpcode() == Opcodes.GOTO;
	}

	private boolean isConditionalJump(AbstractInsnNode movedNode) {
		return movedNode instanceof JumpInsnNode && movedNode.getOpcode() != Opcodes.GOTO;
	}

	private AbstractInsnNode checkLogicGateExpressionIsOnTop(ConditionalExpression exp, AbstractInsnNode movedNode) {
		ExpressionStack thenBranchBackup = exp.getThenBranch().duplicate();
		ConditionalExpression innerExp = makeConditionalExpression((JumpInsnNode)movedNode, exp.getThenBranch());
		exp.getThenBranch().push(innerExp);
		if (exp.containsLogicGateExpression()) {
			movedNode = movedNode.getNext();
		} else {
			exp.setThenBranch(thenBranchBackup);
			movedNode = visitJumpInsnNode((JumpInsnNode)movedNode, exp.getThenBranch());
		}
		return movedNode;
	}

	private ConditionalExpression makeConditionalExpression(JumpInsnNode node, ExpressionStack stack) {
		ConditionalExpression exp = null;

		int jumpDestination = stack.getLabelId(node.label.getLabel());
		int opCode = node.getOpcode();
		System.out.println("L" + jumpDestination);

		if (isBetween(opCode, Opcodes.IF_ICMPEQ, Opcodes.IF_ACMPNE)) {
			exp = new MultiConditional(node, jumpDestination, stack.pop(), stack.pop(), mStack.getNew());
		} else if (isBetween(opCode, Opcodes.IFEQ, Opcodes.IFLE)) {
			Expression stackTop = stack.peek();
			if (stackTop instanceof MultiConditional && !((MultiConditional) stackTop).isJumpDestinationSet()) {
				exp = (MultiConditional)stack.pop();
				exp.setJumpDestination(jumpDestination);
				exp.setInstruction(node);
			} else {
				exp = new SingleConditional(node, jumpDestination, stack.pop(), mStack.getNew());
			}
		} else if (isBetween(opCode, Opcodes.IFNULL, Opcodes.IFNONNULL)) {
			exp = new MultiConditional(node, jumpDestination, new PrimaryExpression("null", DataType.UNKNOWN), stack.pop(), mStack.getNew());
		} else if (opCode == Opcodes.GOTO) {
			int frameIndex = stack.getExpressionIndexOfFrame(jumpDestination);
			if (frameIndex != -1) {
				exp = new UnconditionalJump(node, jumpDestination, stack.substack(frameIndex, stack.size()));
			} else {
				exp = new UnconditionalJump(node, jumpDestination);
			}
		}
		if (opCode != Opcodes.GOTO && exp != null && node.getNext() != null && node.getNext() instanceof LabelNode) {
			exp.setThenBranchStart(stack.getLabelId(((LabelNode) node.getNext()).getLabel()));
		}
		return exp;
	}

	private void visitLabelNode(LabelNode node, ExpressionStack stack) {
		printNodeInfo(node);
		mLabel = node.getLabel();
		stack.addLabel(mLabel);
		mCurrentLabel = stack.getLabelId(mLabel);
		mVisitedLabels.add(mCurrentLabel);
		System.out.println("LABEL: " + "L" + mCurrentLabel);
	}

	// LDC
	private void visitLdcInsnNode(LdcInsnNode node, ExpressionStack stack) {
		printNodeInfo(node);
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
			type = DataType.getType("java.lang.String");
		} else {
			constant = Util.getType(((Type) constant).getDescriptor()).toString() + ".class"; //todo think is this correct?
			type = DataType.getType("java.lang.Class");
		}
		stack.push(new PrimaryExpression(node, constant, type));
	}

	private void visitIincInsnNode(IincInsnNode node, ExpressionStack stack) {
		printNodeInfo(node);
		LocalVariable variable = mLocalVariables.get(node.var);
		if (node.getPrevious() != null && node.getPrevious().getOpcode() == Opcodes.ILOAD) {
			stack.push(new UnaryExpression(node, variable, DataType.INT, UnaryExpression.OpPosition.POSTFIX));
			return;
		}
		if (node.getNext() != null && node.getNext().getOpcode() == Opcodes.ILOAD) {
			stack.push(new UnaryExpression(node, variable, DataType.INT, UnaryExpression.OpPosition.PREFIX));
			return;
		}
		stack.push(new AssignmentExpression(node, new LeftHandSide(node, variable), new PrimaryExpression(node, node.incr, DataType.INT)));
	}

	//	TABLESWITCH
	private AbstractInsnNode visitTableSwitchInsnNode(TableSwitchInsnNode node, ExpressionStack stack) {
		printNodeInfo(node);
		int defaultLabel = stack.getLabelId(node.dflt.getLabel());

		Map<Integer, String> labelCaseMap = new HashMap<>();

		for (int i = 0; i <= node.max - node.min; i++) {
			int labelId = stack.getLabelId(((LabelNode) node.labels.get(i)).getLabel());
			String caseKey = String.valueOf(node.min + i);
			labelCaseMap.put(labelId, caseKey);
		}
		labelCaseMap.put(defaultLabel, SwitchExpression.CaseExpression.DEFAULT);

		SwitchExpression switchExp = new SwitchExpression(node);
		AbstractInsnNode movedNode = updateSwitchWithCases(switchExp, node, defaultLabel, labelCaseMap);
		stack.push(switchExp);
		return movedNode;
	}

	private AbstractInsnNode updateSwitchWithCases(SwitchExpression switchExp, AbstractInsnNode movedNode, int defaultLabel, Map<Integer, String> labelCaseMap) {
		ExpressionStack caseStack = mStack.getNew();
		int currentLabel = caseStack.getLabelId(mLabel);
		int switchEndLabel = -1;
		SwitchExpression.CaseExpression caseExpression = null;

		while (currentLabel != switchEndLabel && (movedNode = movedNode.getNext()) != null) {
			movedNode = pushNodeToStackAsExpression(movedNode, caseStack);
			currentLabel = caseStack.getLabelId(mLabel);

			if (labelCaseMap.containsKey(currentLabel) && caseExpression == null) {
				caseExpression = new SwitchExpression.CaseExpression(labelCaseMap.get(currentLabel), currentLabel, defaultLabel, caseStack);
			}

			if (caseExpression != null && labelCaseMap.containsKey(currentLabel) && caseExpression.getLabel() != currentLabel) {
				if (caseStack.peek() instanceof UnconditionalJump) {
					UnconditionalJump jump = (UnconditionalJump) caseStack.pop();
					switchEndLabel = jump.getJumpDestination();
					caseStack.push(new BreakExpression(jump));
				}
				switchExp.addCase(caseExpression);
				caseStack = mStack.getNew();
				caseExpression = null;
			}

		}
		if (caseExpression != null) {
			switchExp.addCase(caseExpression);
		}
		return movedNode;
	}

	// LOOKUPSWITCH
	private AbstractInsnNode visitLookupSwitchInsnNode(LookupSwitchInsnNode node, ExpressionStack stack) {
		printNodeInfo(node);
		int defaultLabel = stack.getLabelId(node.dflt.getLabel());
		Map<Integer, String> labelCaseMap = new HashMap<>();
		for (int i = 0; i < node.labels.size(); i++) {
			int labelId = stack.getLabelId(((LabelNode) node.labels.get(i)).getLabel());
			String caseKey = String.valueOf(node.keys.get(i));
			labelCaseMap.put(labelId, caseKey);
		}
		labelCaseMap.put(defaultLabel, SwitchExpression.CaseExpression.DEFAULT);

		SwitchExpression switchExp = new SwitchExpression(node);
		AbstractInsnNode movedNode = updateSwitchWithCases(switchExp, node, defaultLabel, labelCaseMap);
		stack.push(switchExp);
		return movedNode;
	}

	//	MULTIANEWARRAY
	private void visitMultiANewArrayInsnNode(MultiANewArrayInsnNode node, ExpressionStack stack) {
		printNodeInfo(node);
		stack.push(new ArrayCreationExpression(node));
	}

	private void visitFrameNode(FrameNode node, ExpressionStack stack) {
		System.out.println("FRAME:");
		printNodeInfo(node);
		System.out.println("local: " + Arrays.deepToString(node.local.toArray()));
		System.out.println("stack: " + Arrays.deepToString(node.stack.toArray()));

		mFrameLabel = mCurrentLabel;
		stack.addFrame(mCurrentLabel);
	}

	private void visitLineNumberNode(LineNumberNode node, ExpressionStack stack) {
		System.out.println("LINE: " + node.line);
		printNodeInfo(node);
		stack.setLineNumber(node.line);
	}

	private void printNodeInfo(AbstractInsnNode node) {
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
		System.out.println("STACK: " + mStack.size());
		String result = "code: " + opCode + " " + fields;
		System.out.println(result);
	}

	/**
	 * min and max are inclusive
	 * @return true if num is between min and max
	 */
	private static boolean isBetween(int num, int min, int max) {
		return num >= min && num <= max;
	}

}
