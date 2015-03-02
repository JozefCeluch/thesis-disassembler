package com.thesis.file;

import com.thesis.block.Block;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Parser {

    private Reader mReader;
	private static Parser mParser;
	private Map<String, String> mInnerClassMap; // <full inner class name, displayed inner class name>

	public static Parser createInstance(String directory) {
		mParser = new Parser(directory);
		return mParser;
	}

	public static Parser getInstance() {
		return mParser;
	}

    private Parser(String directory) {
        mReader = new Reader(directory);
		mInnerClassMap = new HashMap<>();
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
			ClassVisitor classVisitor = new TraceClassVisitor(new PrintWriter(System.out));
			classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
			disassembler.disassembleClass(classNode, parent);
			disassembler.print();
			return stringWriter.toString();
		} catch (IOException e) {
			e.printStackTrace(); //TODO
			return null;
		}
	}

	public void addInnerClassName(String fullName, String displayName) {
		if (!mInnerClassMap.containsKey(fullName)) {
			mInnerClassMap.put(fullName, displayName);
		}
	}

	public String getInnerClassDisplayName(String fullName) {
		String result = mInnerClassMap.get(fullName);

		return result != null ? result : fullName;
	}
}
