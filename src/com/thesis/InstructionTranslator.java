package com.thesis;

import com.thesis.block.Block;
import com.thesis.block.Statement;
import com.thesis.common.DataType;
import com.thesis.common.Util;
import com.thesis.expression.*;
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
	private static final HashMap<Label, Integer> mLabels = new HashMap<>();
	private List<Block> mStatements;
	private Map<Integer, LocalVariable> mLocalVariables;
	private int mLine;
	private Label mLabel;
	private TryCatchManager mTryCatchManager;

	public InstructionTranslator(MethodNode method, List<Block> statements, Map<Integer, LocalVariable> arguments) {
		mStatements = statements;
		mStack = new ExpressionStack();
		mMethod = method;
		mLocalVariables = new HashMap<>();
		copyLocalVariables();
		mLocalVariables.putAll(arguments);
		mTryCatchManager = TryCatchManager.newInstance(method.tryCatchBlocks, mStack);
	}

	private void copyLocalVariables() {
		if (mMethod.localVariables.size() > 0) {
			for (Object var : mMethod.localVariables) {
				LocalVariableNode variable = (LocalVariableNode) var;
				mLocalVariables.put(variable.index, new LocalVariable(variable));
			}
		}
	}

	public static HashMap<Label, Integer> getLabels() {
		return mLabels;
	}

	public void addCode() {
		System.out.println(" ");
		System.out.println("METHOD: " + mMethod.name);
		AbstractInsnNode node = mMethod.instructions.getFirst();
		while (node != null && node.getNext() != null) {
			node = node.getNext();
			node = pushNodeToStackAsExpression(node, mStack);
		}
		addLocalVariablesAssignments();
		//todo do some improvements of the expressions on the expression stack here
		StatementCreator sc = new StatementCreator(mStack);
		sc.createStatements();
		mStatements.addAll(sc.getStatements());
	}

	private AbstractInsnNode pushNodeToStackAsExpression(AbstractInsnNode node, ExpressionStack stack) {
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
		stack.setLineNumber(mLine);
		stack.addLabel(mLabel);
		return node;
	}

	private void addLocalVariablesAssignments() {
		List<Block> localVars = mLocalVariables.values().stream()
				.filter(variable -> !variable.isArgument())
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
			stack.push(new MultiConditional(node, ConditionalExpression.NO_DESTINATION, new ExpressionStack()));
		} else if (isBetween(opCode, Opcodes.IADD, Opcodes.LXOR)) {
			stack.push(new ArithmeticExpression(node));
		} else if (isBetween(opCode, Opcodes.IRETURN, Opcodes.RETURN)) {
			stack.push(new ReturnExpression(node));
		} else if (opCode == Opcodes.ARRAYLENGTH) {
			stack.push(new ArrayLengthExpression(node));
		} else if (opCode == Opcodes.ATHROW) {
			//TODO
		} else if (opCode == Opcodes.MONITORENTER) {
			//TODO
		} else if (opCode == Opcodes.MONITOREXIT) {
			//TODO
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
				mLocalVariables.put(node.var, new LocalVariable("var" + node.var, node.var));
			}
			LocalVariable localVar = mLocalVariables.get(node.var);
			stack.push(new AssignmentExpression(node, new LeftHandSide(node, localVar)));
			//todo assignment to fields and static variables
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
	 * GOTO, JSR.
	 */
	private AbstractInsnNode visitJumpInsnNode(JumpInsnNode node, ExpressionStack stack) {
		//TODO REFACTOR AND GENERALISE THE WHOLE METHOD!!!
		printNodeInfo(node);
		int jumpDestination = stack.getLabelId(node.label.getLabel());
		int opCode = node.getOpcode();
		AbstractInsnNode movedNode = node;
		ConditionalExpression exp = null;
		System.out.println("L" + jumpDestination);

		if (isBetween(opCode, Opcodes.IF_ICMPEQ, Opcodes.IF_ACMPNE)) {
			exp = new MultiConditional(node, jumpDestination, new ExpressionStack());
			stack.push(exp);
			System.out.println("CREATED MultiConditional EXP");

		} else if (isBetween(opCode, Opcodes.IFEQ, Opcodes.IFLE)) {
			Expression stackTop = stack.peek();
			if (stackTop instanceof MultiConditional && ((MultiConditional) stackTop).getConditionalJumpDest() == -1) {
				exp = (MultiConditional)stackTop;
				exp.setConditionalJumpDest(jumpDestination);
				exp.setInstruction(node);
			} else {
				exp = new SingleConditional(node, jumpDestination, new ExpressionStack());
				stack.push(exp);
			}
			System.out.println("CREATED SingleConditional EXP");

		} else if (isBetween(opCode, Opcodes.IFNULL, Opcodes.IFNONNULL)) {
			exp = new MultiConditional(node, jumpDestination, new ExpressionStack());
			stack.push(new PrimaryExpression("null", DataType.UNKNOWN));
			stack.push(exp);
		} else if (opCode == Opcodes.GOTO) {
			stack.push(new UnconditionalJump(node, jumpDestination));
		}
		// JSR is deprecated since Java 6
		int elseBranchEnd = 0;
		while(exp != null && movedNode.getOpcode() != Opcodes.GOTO && stack.getLabelId(mLabel) != jumpDestination) {
			movedNode = movedNode.getNext();
			movedNode = pushNodeToStackAsExpression(movedNode, exp.getThenBranch());

			if (movedNode instanceof JumpInsnNode) {
				elseBranchEnd = stack.getLabelId(((JumpInsnNode) movedNode).label.getLabel());
				exp.setGoToDest(elseBranchEnd);
				if (exp.getThenBranch().size() == 2) {
					Expression expression = exp.getThenBranch().get(0);
					if (expression instanceof PrimaryExpression &&
							(((PrimaryExpression) expression).getValue().equals(1) || ((PrimaryExpression) expression).getValue().equals(0))) {
						expression.setType(DataType.BOOLEAN);
					}
				}
				break;
			}
			Expression branchTop = exp.getThenBranch().peek();
			Expression stackTop = stack.peek();
			if (branchTop != null && branchTop instanceof ConditionalExpression && stackTop instanceof ConditionalExpression && ((ConditionalExpression) stackTop).getConditionalJumpDest() == ((ConditionalExpression) branchTop).getConditionalJumpDest()) {
				stack.push(new LogicGateExpression((ConditionalExpression) exp.getThenBranch().pop()));
				break;
			}
		}

		if (exp != null && elseBranchEnd != 0 && elseBranchEnd != jumpDestination) {
			while(stack.getLabelId(mLabel) != elseBranchEnd){
				movedNode = movedNode.getNext();
				movedNode = pushNodeToStackAsExpression(movedNode, exp.getElseBranch());
			}
			if (exp.getElseBranch().size() == 1) {
				Expression expression = exp.getElseBranch().get(0);
				if (DataType.BOOLEAN.equals(exp.getThenBranch().get(0).getType())) {
					expression.setType(DataType.BOOLEAN);
				}
			}
		}
		if (exp!= null && exp.isTernaryExpression()) {
			stack.push(new TernaryExpression((ConditionalExpression)stack.pop()));
		}
		return movedNode;
	}

	private void visitLabelNode(LabelNode node, ExpressionStack stack) {
		printNodeInfo(node);
		mLabel = node.getLabel();
		if(mLabels.containsKey(mLabel)) {
			System.out.println("LABEL: " + "L" + mLabels.get(mLabel));
		}
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
		ExpressionStack caseStack = new ExpressionStack();
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
					switchEndLabel = jump.getConditionalJumpDest();
					caseStack.push(new BreakExpression(jump));
				}
				switchExp.addCase(caseExpression);
				caseStack = new ExpressionStack();
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
	}

	private void visitLineNumberNode(LineNumberNode node, ExpressionStack stack) {
		printNodeInfo(node);
		mLine = node.line;

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
