package com.thesis.file;

import jdk.internal.org.objectweb.asm.util.Printer;
import org.objectweb.asm.tree.*;

import java.util.List;

public class MethodDisassembler extends Disassembler{
	private static final String BLOCK_START = " " + LEFT_BRACKET + NEW_LINE;
	private static final String BLOCK_END = RIGHT_BRACKET + NEW_LINE;
	MethodNode mMethodNode;

	public MethodDisassembler(MethodNode methodNode) {
		super();
		mMethodNode = methodNode;
	}

	public List<Object> disassemble() {
		clearBuffer();
		text.add(BLOCK_START);
		InsnList instructions = mMethodNode.instructions;
		AbstractInsnNode node = instructions.getFirst();
		while (node != null) {
			int opCode;
			switch (node.getType()) {
				case AbstractInsnNode.LINE:
					buf.append(((LineNumberNode) node).line);
					break;
				case AbstractInsnNode.VAR_INSN:
					opCode = node.getOpcode();
					if (opCode > -1) {
						buf.append(Printer.OPCODES[opCode]);
					}
					buf.append(" ").append(((VarInsnNode)node).var);
					break;
				case AbstractInsnNode.METHOD_INSN:
					opCode = node.getOpcode();
					if (opCode > -1) {
						buf.append(Printer.OPCODES[opCode]);
					}
					buf.append(" ").append(((MethodInsnNode)node).owner).append(".").append(((MethodInsnNode)node).name);
					break;
				case AbstractInsnNode.LABEL:
					break;
				default:
					buf.append(node).append(": ");
					opCode = node.getOpcode();
					if (opCode > -1) {
						buf.append(Printer.OPCODES[opCode]);
					}
			}
			buf.append(NEW_LINE);
			node = node.getNext();

		}
		text.add(buf.toString());
		text.add(BLOCK_END);
		return text;
	}
}
