package com.thesis;

import com.thesis.block.Block;
import com.thesis.block.ReturnStatement;
import com.thesis.block.Statement;
import com.thesis.expression.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Stack;

public class InstructionTranslator {

	private final MethodNode mMethod;
	Stack<Expression> mStack;
	List<Block> mStatements;
	StringBuffer buf;

	public InstructionTranslator(MethodNode method, List<Block> statements) {
		buf = new StringBuffer();
		mStatements = statements;
		mStack = new Stack<>();
		mMethod = method;
	}

	public void addCode() {
		AbstractInsnNode node = mMethod.instructions.getFirst();
		while (node != null) {
			int opCode;
			switch (node.getType()) {
				case AbstractInsnNode.INSN:
					visitInsnNode((InsnNode) node);
					break;
				case AbstractInsnNode.INT_INSN:
					visitIntInsnNode((IntInsnNode) node);
					break;
				case AbstractInsnNode.VAR_INSN:
					visitVarInsnNode((VarInsnNode) node);
					break;
				case AbstractInsnNode.TYPE_INSN:
					visitTypeInsnNode((TypeInsnNode) node);
					break;
				case AbstractInsnNode.FIELD_INSN:
					visitFieldInsnNode((FieldInsnNode) node);
					break;
				case AbstractInsnNode.METHOD_INSN:
					visitMethodInsnNode((MethodInsnNode) node);
					break;
				case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
					visitInvokeDynamicInsnNode((InvokeDynamicInsnNode) node);
					break;
				case AbstractInsnNode.JUMP_INSN:
					visitJumpInsnNode((JumpInsnNode) node);
					break;
				case AbstractInsnNode.LABEL:
					visitLabelNode((LabelNode) node);
					break;
				case AbstractInsnNode.LDC_INSN:
					visitLdcInsnNode((LdcInsnNode) node);
					break;
				case AbstractInsnNode.IINC_INSN:
					visitIincInsnNode((IincInsnNode) node);
					break;
				case AbstractInsnNode.TABLESWITCH_INSN:
					visitTableSwitchInsnNode((TableSwitchInsnNode) node);
					break;
				case AbstractInsnNode.MULTIANEWARRAY_INSN:
					visitMultiANewArrayInsnNode((MultiANewArrayInsnNode) node);
					break;
				case AbstractInsnNode.FRAME:
					visitFrameNode((FrameNode) node);
					break;
				case AbstractInsnNode.LINE:
					visitLineNumberNode((LineNumberNode) node);
					break;
				default:
					buf.append(node).append(": ");
					opCode = node.getOpcode();
					if (opCode > -1) {
						buf.append(Printer.OPCODES[opCode]);
					}
			}
			buf.append("\n");
			node = node.getNext();
		}
	}

	/**
	 *            NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1,
	 *            ICONST_2, ICONST_3, ICONST_4, ICONST_5, LCONST_0, LCONST_1,
	 *            FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD,
	 *            LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
	 *            IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE,
	 *            SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1,
	 *            DUP2_X2, SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB,
	 *            IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM,
	 *            FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR,
	 *            IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D,
	 *            L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S,
	 *            LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN,
	 *            DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER,
	 *            or MONITOREXIT.
	 */
	private void visitInsnNode(InsnNode node) {
		String opCode = getOpcodeString(node.getOpcode());
		if (mStack.size() >= 2) {
			Expression ex2 = mStack.pop();
			Expression ex1 = mStack.pop();
			mStack.push(new ArithmeticExpression(node, ex1, ex2));
		}
		if (opCode.contains("CONST")) {
			int valPos = opCode.lastIndexOf("_");
			String val = opCode.substring(valPos + 1);
			mStack.push(new PrimaryExpression(val.toLowerCase()));
		}
		if (opCode.contains("RETURN")) {
			if (mStack.size() == 1) mStatements.add(new ReturnStatement(mStack.pop()));
		}
	}

	//	BIPUSH, SIPUSH or NEWARRAY
	private void visitIntInsnNode(IntInsnNode node) {
		methodNotImplemented(node);
	}

//	ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET
	private void visitVarInsnNode(VarInsnNode node) {
		String opCode = getOpcodeString(node.getOpcode());
		if (opCode.endsWith("LOAD")) {
			if (mMethod.localVariables.size() > node.var) //todo here can by index out of bounds sometimes, why??!?!
				mStack.push(new PrimaryExpression(mMethod.localVariables.get(node.var)));
		}
		if (opCode.endsWith("STORE")) {
			Object localVar = mMethod.localVariables.get(node.var);
			PrimaryExpression leftSide = new PrimaryExpression(localVar);
			mStatements.add(new Statement(new AssignmentExpression(node, leftSide, mStack.pop())));

		}
	}

//	NEW, ANEWARRAY, CHECKCAST or INSTANCEOF
	private void visitTypeInsnNode(TypeInsnNode node) {
		methodNotImplemented(node);
	}

//	GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD
	private void visitFieldInsnNode(FieldInsnNode node) {
		methodNotImplemented(node);
	}

//	INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE
	private void visitMethodInsnNode(MethodInsnNode node) {
		methodNotImplemented(node);
	}

//	INVOKEDYNAMIC
	private void visitInvokeDynamicInsnNode(InvokeDynamicInsnNode node) {
		methodNotImplemented(node);
	}

	/**
	 * IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ,
	 * IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
	 * IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.
	 */
	private void visitJumpInsnNode(JumpInsnNode node) {
		methodNotImplemented(node);
	}

	private void visitLabelNode(LabelNode node) {
		methodNotImplemented(node);
	}
	// LDC
	private void visitLdcInsnNode(LdcInsnNode node) {
		buf.append(" ").append(node.cst);
		mStack.push(new PrimaryExpression(node.cst));
	}

	private void visitIincInsnNode(IincInsnNode node) {
		methodNotImplemented(node);
	}

//	TABLESWITCH
	private void visitTableSwitchInsnNode(TableSwitchInsnNode node) {
		methodNotImplemented(node);
	}

//	MULTIANEWARRAY
	private void visitMultiANewArrayInsnNode(MultiANewArrayInsnNode node) {
		methodNotImplemented(node);
	}

	private void visitFrameNode(FrameNode node) {
		methodNotImplemented(node);
	}

	private void visitLineNumberNode(LineNumberNode node) {
		methodNotImplemented(node);
	}

	private static String getOpcodeString(int opCode) {
		String op = "";
		if (opCode > -1) {
			op = Printer.OPCODES[opCode];
		}
		return op;
	}

	private void methodNotImplemented(AbstractInsnNode node) {
		String opCode = getOpcodeString(node.getOpcode());
		if (opCode.isEmpty()) return;
		String fields = "";
		for (Field field : node.getClass().getFields()) {
			if (!(field.getName().contains("INSN") || field.getName().contains("LABEL")|| field.getName().contains("FRAME") || field.getName().contains("LINE"))) {
				try {
					fields += field.getName() + "= " + field.get(node);
					fields += "; ";
				} catch (IllegalAccessException e) {
					System.out.println(field.getName() + " is inaccessible");
				}
			}

		}
		String result = "code: " + opCode + " " + fields;
		System.out.println(result);
	}
}
