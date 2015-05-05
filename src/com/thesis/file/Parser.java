package com.thesis.file;

import com.thesis.block.Block;
import com.thesis.block.ClassBlock;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
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
		ClassReader classReader = getClassReader(file);
		if (classReader == null) {
			return "ERROR";
		}

		ClassVisitor classVisitor = new TraceClassVisitor(new PrintWriter(System.out));
		classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

		Writer stringWriter = new StringWriter();
		Writer printWriter = new PrintWriter(stringWriter);
		ClassBlock classBlock = disassembleClass(classReader, null);
		try {
			classBlock.write(printWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringWriter.toString();
    }

	public ClassBlock parseInnerClass(String file, Block parent) {
		file = file.endsWith(".class") ? file : file + ".class";
		return disassembleClass(getClassReader(file), parent);
	}

	private ClassBlock disassembleClass(ClassReader classReader, Block parent) {
		ClassNode classNode = new ClassNode();
		classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
		ClassBlock classBlock = new ClassBlock(classNode, parent);
		classBlock.disassemble();
		return classBlock;
	}

	private ClassReader getClassReader(String file) {
		try(InputStream is = mReader.openClassFile(file)) {
			return new ClassReader(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace(); //TODO
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
