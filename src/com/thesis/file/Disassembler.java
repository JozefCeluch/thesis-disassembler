package com.thesis.file;

import com.thesis.block.Block;
import com.thesis.block.ClassBlock;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.io.PrintWriter;

public class Disassembler {
	private PrintWriter pw;
	ClassBlock classBlock;

	public Disassembler(PrintWriter printWriter) {
		pw = printWriter;
	}

	public void disassembleClass(ClassNode classNode, Block parent) {
		//todo imports
		classBlock = new ClassBlock(classNode, parent);
		classBlock.disassemble();
	}

	public void print() {
		try {
			classBlock.write(pw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
