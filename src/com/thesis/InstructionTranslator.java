package com.thesis;

import com.thesis.block.Block;
import com.thesis.block.Statement;
import com.thesis.common.Util;
import com.thesis.expression.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class InstructionTranslator {

	private final MethodNode mMethod;
	private ExpressionStack mStack;
	private HashMap<Label, Integer> mLabels;
	private List<Block> mStatements;
	private StringBuffer buf;
	private Map<Integer, LocalVariable> mLocalVariables;
	private Stack<ExpressionStack> mActiveStacks;

	public InstructionTranslator(MethodNode method, List<Block> statements, Map<Integer, LocalVariable> arguments) {
		buf = new StringBuffer();
		mStatements = statements;
		mLabels = new HashMap<>();
		mStack = new ExpressionStack(mLabels);
		mMethod = method;
		mLocalVariables = new HashMap<>();
		copyLocalVariables();
		mLocalVariables.putAll(arguments);
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
			node = visitCorrectNode(node, mStack);
			node = node.getNext();
		}
		addLocalVariablesAssignments();
	}

	private AbstractInsnNode visitCorrectNode(AbstractInsnNode node, ExpressionStack stack) {
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
				visitTableSwitchInsnNode((TableSwitchInsnNode) node, stack);
				break;
			case AbstractInsnNode.MULTIANEWARRAY_INSN:
				visitMultiANewArrayInsnNode((MultiANewArrayInsnNode) node, stack);
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

	private void addLocalVariablesAssignments() {
		List<Block> localVars = mLocalVariables.values()
				.stream()
				.filter(variable -> !variable.isArgument())
				.map(variable -> new Statement(new VariableDeclarationExpression(variable)))
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
	private void visitInsnNode(InsnNode node, ExpressionStack mStack) {
		printNodeInfo(node);
		String opCode = Util.getOpcodeString(node.getOpcode());
		if (opCode.contains("LOAD")) {
			mStack.push(new ArrayAccessExpression(node));
		}
		if (opCode.contains("CONST")) {
			mStack.push(new PrimaryExpression(node));
		}
		if (opCode.contains("RETURN")) {
			mStack.push(new ReturnExpression(node));
		}
		if (node.getOpcode() <= 131 && node.getOpcode() >= 96) {
			mStack.push(new ArithmeticExpression(node));
		}
	}

	//	BIPUSH, SIPUSH or NEWARRAY
	private void visitIntInsnNode(IntInsnNode node, ExpressionStack mStack) {
		printNodeInfo(node);
		String opCode = Util.getOpcodeString(node.getOpcode());
		switch (opCode) {
			case "BIPUSH":
				mStack.push(new PrimaryExpression(node, node.operand, "int"));
				break;
			case "SIPUSH":
				mStack.push(new PrimaryExpression(node, node.operand, "short"));
				break;
			case "NEWARRAY":
				mStack.push(new ArrayCreationExpression(node));
				break;
			default:
				throw new IllegalStateException("Unexpected OpCode");
		}
	}

	//	ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET
	private void visitVarInsnNode(VarInsnNode node, ExpressionStack mStack) {
		printNodeInfo(node);
		String opCode = Util.getOpcodeString(node.getOpcode());
		if (opCode.endsWith("LOAD")) {
			LocalVariable var = mLocalVariables.get(node.var);
			mStack.push(new PrimaryExpression(node, var, var.getType()));
		}
		if (opCode.endsWith("STORE")) {
			if (!mLocalVariables.containsKey(node.var)) {
				mLocalVariables.put(node.var, new LocalVariable("var" + node.var, node.var));
			}
			LocalVariable localVar = mLocalVariables.get(node.var);
			mStack.push(new AssignmentExpression(node, new LeftHandSide(node, localVar)));
			//todo assignment to fields and static variables
		}
	}

	//	NEW, ANEWARRAY, CHECKCAST or INSTANCEOF
	private void visitTypeInsnNode(TypeInsnNode node, ExpressionStack mStack) {
		printNodeInfo(node);
	}

	//	GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD
	private void visitFieldInsnNode(FieldInsnNode node, ExpressionStack mStack) {
		printNodeInfo(node);
	}

	//	INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE
	private void visitMethodInsnNode(MethodInsnNode node, ExpressionStack mStack) {
		printNodeInfo(node);
	}

	//	INVOKEDYNAMIC
	private void visitInvokeDynamicInsnNode(InvokeDynamicInsnNode node, ExpressionStack mStack) {
		printNodeInfo(node);
	}

	/**
	 * IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IFNULL or IFNONNULL
	 * IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE,
	 * GOTO, JSR.
	 */
	private AbstractInsnNode visitJumpInsnNode(JumpInsnNode node, ExpressionStack mStack) {
		if (mActiveStacks == null) mActiveStacks = new Stack<>();
		printNodeInfo(node);
		int jumpDestination = mStack.getLabelId(node.label.getLabel());
		int opcode = node.getOpcode();
		if (opcode >= 159 && opcode <= 166) { //between IF_ICMPEQ and IF_ACMPNE
			MultiConditional exp = new MultiConditional(node, jumpDestination);
			exp.thenBranch = new ExpressionStack(mLabels);
			AbstractInsnNode branchNode = node;
			mStack.push(exp);
			int elseEndLabel = 0;
			while(branchNode.getOpcode() != 167) {
				branchNode = branchNode.getNext();
				if((branchNode instanceof LabelNode) && jumpDestination == mStack.getLabelId(((LabelNode) branchNode).getLabel())) break;
				visitCorrectNode(branchNode, exp.thenBranch);
				if (branchNode instanceof JumpInsnNode) elseEndLabel = mStack.getLabelId(((JumpInsnNode) branchNode).label.getLabel());
			}
			if (elseEndLabel > jumpDestination) {
				exp.elseBranch = new ExpressionStack(mLabels);
				while(true){
					if ( branchNode instanceof LabelNode ){
						int label = mStack.getLabelId(((LabelNode) branchNode).getLabel());
						if (label >= elseEndLabel) break;
					}
					branchNode = branchNode.getNext();
					visitCorrectNode(branchNode, exp.elseBranch);
				}
			}
			return branchNode;
//			mActiveStacks.push(mStack);
//			mStack = exp.thenBranch;
		}

		if (opcode >= 153 && opcode <= 158) { // between IFEQ and IFLE
			SingleConditional exp = new SingleConditional(node, jumpDestination);
			exp.thenBranch = new ExpressionStack(mLabels);
			mStack.push(exp);

			AbstractInsnNode branchNode = node;
			while(branchNode.getOpcode() != 167) {
				branchNode = branchNode.getNext();
				if((branchNode instanceof LabelNode) && jumpDestination == mStack.getLabelId(((LabelNode) branchNode).getLabel())) break;
				visitCorrectNode(branchNode, exp.thenBranch);
			}
			return branchNode;
//			mActiveStacks.push(mStack);
//			mStack = exp.thenBranch;
		}

		if (Util.getOpcodeString(opcode).equals("GOTO")) {
			mStack.push(new UnconditionalJump(node, jumpDestination));
		}
		// todo ifnull, innonnull, jsr

		System.out.println("L" + jumpDestination);
		return node;
	}

	private void visitLabelNode(LabelNode node, ExpressionStack mStack) {
		printNodeInfo(node);

//		if (!mStack.isEmpty()) {
//			StackExpression stackTop = mStack.peek();
//			if (stackTop.expression instanceof AssignmentExpression || stackTop.expression instanceof ReturnExpression) {
//				mStack.pop();
//				mStatements.add(new Statement(stackTop.expression));
//			}
//			if (mStack.size() >= 2 && stackTop.expression instanceof UnconditionalJump) { //todo now handles only logical expressions
//				mStack.pop();
//				mStack.pop();
//			}
//		}
//		mStack.push(new StackExpression(getLabelId(node.getLabel())));
		mStack.addLabel(node.getLabel());
	}

	// LDC
	private void visitLdcInsnNode(LdcInsnNode node, ExpressionStack mStack) {
		printNodeInfo(node);
		buf.append(" ").append(node.cst);
		String type;
		if (node.cst instanceof Integer) {
			type = "int";
		} else if (node.cst instanceof Float) {
			type = "float";
		} else if (node.cst instanceof Double) {
			type = "double";
		} else if (node.cst instanceof Long) {
			type = "long";
		} else if (node.cst instanceof String) {
			type = "String";
		} else {
			type = Util.getType(((Type) node.cst).getDescriptor());
		}
		mStack.push(new PrimaryExpression(node, node.cst, type));
	}

	private void visitIincInsnNode(IincInsnNode node, ExpressionStack mStack) {
		printNodeInfo(node);
		LocalVariable variable = mLocalVariables.get(node.var);
		if (node.getPrevious() != null && node.getPrevious().getOpcode() == 21) {
			mStack.push(new UnaryExpression(node, variable, "int", UnaryExpression.OpPosition.POSTFIX));
			return;
		}
		if (node.getNext() != null && node.getNext().getOpcode() == 21) {
			mStack.push(new UnaryExpression(node, variable, "int", UnaryExpression.OpPosition.PREFIX));
			return;
		}
		mStack.push(new AssignmentExpression(node, new LeftHandSide(node, variable), new PrimaryExpression(node, node.incr, "int")));
	}

	//	TABLESWITCH
	private void visitTableSwitchInsnNode(TableSwitchInsnNode node, ExpressionStack mStack) {
		printNodeInfo(node);
	}

	//	MULTIANEWARRAY
	private void visitMultiANewArrayInsnNode(MultiANewArrayInsnNode node, ExpressionStack mStack) {
		printNodeInfo(node);
	}

	private void visitFrameNode(FrameNode node, ExpressionStack mStack) {
//		if (mActiveStacks != null && !mActiveStacks.isEmpty()) {
//			if (mStack.peek() != null && mStack.peek() instanceof UnconditionalJump) {
//				mStack = new ExpressionStack(mLabels);
//				((ConditionalExpression)mActiveStacks.peek().peek()).elseBranch = mStack;
//			} else {
//				mStack = mActiveStacks.pop();
//			}
//		}
		System.out.println("FRAME:");
		printNodeInfo(node);
		System.out.println("local: " + Arrays.deepToString(node.local.toArray()));
		System.out.println("stack: " + Arrays.deepToString(node.stack.toArray()));
	}

	private void visitLineNumberNode(LineNumberNode node, ExpressionStack mStack) {
		printNodeInfo(node);
//		mCurrentLine = node.line;
//		int labelId = getLabelId(node.start.getLabel());
//		System.out.println("line: " + mCurrentLine + ", L" + labelId);
//		StackExpression top = mStack.peek();
//		if (top != null && top.labelId == labelId) {
//			top.line = mCurrentLine;
//		} else {
//			StackExpression exp = new StackExpression(labelId); //todo probably not needed
//			exp.line = mCurrentLine;
//			mStack.push(exp);
//		}
		mStack.setLineNumber(node.line);

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
		String result = "code: " + opCode + " " + fields;
		System.out.println(result);
		System.out.println("STACK: " + mStack.size());
	}

}
