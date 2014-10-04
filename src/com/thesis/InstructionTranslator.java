package com.thesis;

import com.thesis.block.Block;
import com.thesis.block.ReturnStatement;
import com.thesis.block.Statement;
import com.thesis.expression.ArithmeticExpression;
import com.thesis.expression.AssignmentExpression;
import com.thesis.expression.Expression;
import com.thesis.expression.PrimaryExpression;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;

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

	private void visitInsnNode(InsnNode node) {
		int opCode;
		opCode = node.getOpcode();
		String op = "";
		if (opCode > -1) {
			op = Printer.OPCODES[opCode];
			buf.append(op);
		}
		if (mStack.size() >= 2) {
			Expression ex2 = mStack.pop();
			Expression ex1 = mStack.pop();
			mStack.push(new ArithmeticExpression(node, ex1, ex2));
		}
		if (op.contains("CONST")) {
			int valPos = op.lastIndexOf("_");
			String val = op.substring(valPos + 1);
			mStack.push(new PrimaryExpression(val.toLowerCase()));
		}
		if (op.contains("RETURN")) {
			if (mStack.size() == 1) mStatements.add(new ReturnStatement(mStack.pop()));
		}
	}

	private void visitIntInsnNode(IntInsnNode node) {

	}

	private void visitVarInsnNode(VarInsnNode node) {
		int opCode;
		opCode = node.getOpcode();
		if (opCode > -1) {
			String op = Printer.OPCODES[opCode];
			buf.append(op);
			if (op.endsWith("LOAD"))
				mStack.push(new PrimaryExpression(mMethod.localVariables.get(node.var)));
			if (op.endsWith("STORE")) {
				if (node instanceof VarInsnNode) {
					Object localVar = mMethod.localVariables.get(node.var);
					PrimaryExpression leftSide = new PrimaryExpression(localVar);
					mStatements.add(new Statement(new AssignmentExpression(node, leftSide, mStack.pop())));
				} else {
					mStatements.add(new Statement(new AssignmentExpression(node, mStack.pop())));
				}
			}
		}
	}

	private void visitTypeInsnNode(TypeInsnNode node) {

	}

	private void visitFieldInsnNode(FieldInsnNode node) {

	}

	private void visitMethodInsnNode(MethodInsnNode node) {

	}

	private void visitInvokeDynamicInsnNode(InvokeDynamicInsnNode node) {

	}

	private void visitJumpInsnNode(JumpInsnNode node) {

	}

	private void visitLabelNode(LabelNode node) {
	}

	private void visitLdcInsnNode(LdcInsnNode node) {
		int opCode;
		opCode = node.getOpcode();
		if (opCode > -1) {
			buf.append(Printer.OPCODES[opCode]);
		}
		buf.append(" ").append(node.cst);
		mStack.push(new PrimaryExpression(node.cst));
	}

	private void visitIincInsnNode(IincInsnNode node) {

	}

	private void visitTableSwitchInsnNode(TableSwitchInsnNode node) {

	}

	private void visitMultiANewArrayInsnNode(MultiANewArrayInsnNode node) {

	}

	private void visitFrameNode(FrameNode node) {

	}
}
