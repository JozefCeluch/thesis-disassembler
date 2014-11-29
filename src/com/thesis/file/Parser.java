package com.thesis.file;

import com.thesis.block.Block;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.ClassReader;

import java.io.*;

public class Parser {

    private Reader mReader;

    public Parser(String directory) {
        mReader = new Reader(directory);
    }

    public String parseClassFile(String file) {
        return parseClassFile(file, null);
    }

	public String parseClassFile(String file, Block parent) {
		InputStream is = null;
		try {
			is = mReader.openClassFile(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace(); //TODO
		}
		try {
			ClassReader classReader = new ClassReader(is);
			ClassNode classNode = new ClassNode();
			StringWriter stringWriter = new StringWriter();
			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			Disassembler disassembler = new Disassembler(new PrintWriter(stringWriter));
//			ClassVisitor classVisitor = new TraceClassVisitor(new PrintWriter(System.out));
//			classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
			disassembler.disassembleClass(classNode, parent);
			disassembler.print();
			return stringWriter.toString();
		} catch (IOException e) {
			e.printStackTrace(); //TODO
			return null;
		}
	}
}
