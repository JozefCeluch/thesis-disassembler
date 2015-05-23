package com.thesis.file;

import com.thesis.block.Block;
import com.thesis.block.ClassBlock;
import com.thesis.exception.DecompilerException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Disassembler {

    private Reader mReader;
	private static Disassembler mDisassembler;
	private Map<String, String> mInnerClassMap; // <full inner class name, displayed inner class name>

	public static Disassembler createInstance(String directory) {
		mDisassembler = new Disassembler(directory);
		return mDisassembler;
	}

	public static Disassembler getInstance() {
		return mDisassembler;
	}

    private Disassembler(String directory) {
        mReader = new Reader(directory);
		mInnerClassMap = new HashMap<>();
    }

    public DecompilationResult decompileClassFile(String file) throws DecompilerException {
		ClassReader classReader = getClassReader(file);
		ClassBlock classBlock = disassembleClass(classReader, null);
		return new DecompilationResult(classBlock);
    }

	public ClassBlock decompileInnerClass(String file, Block parent) throws DecompilerException {
		file = file.endsWith(".class") ? file : file + ".class";
		return disassembleClass(getClassReader(file), parent);
	}

	private ClassBlock disassembleClass(ClassReader classReader, Block parent) {
		Writer bytecodeStringWriter = new StringWriter();
		ClassVisitor classVisitor = new TraceClassVisitor(new PrintWriter(bytecodeStringWriter));
		classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
		System.out.println(bytecodeStringWriter.toString());

		ClassNode classNode = new ClassNode();
		classReader.accept(classNode, ClassReader. EXPAND_FRAMES);
		ClassBlock classBlock = new ClassBlock(classNode, parent);

		classBlock.setBytecode(bytecodeStringWriter.toString());
		classBlock.disassemble();
		return classBlock;
	}

	private ClassReader getClassReader(String file) throws DecompilerException {
		try(InputStream is = mReader.openClassFile(file)) {
			return new ClassReader(is);
		} catch (IOException e) {
			throw new DecompilerException("Unable to read file", e);
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

	public static class DecompilationResult {
		private ClassBlock mClassBlock;

		private String mJavaCode = null;
		private String mBytecode = null;

		public DecompilationResult(ClassBlock classBlock) {
			mClassBlock = classBlock;
		}

		public String getBytecode() {
			if (mBytecode == null) {
				mBytecode = mClassBlock.getBytecode();
			}
			return mBytecode;
		}

		public String getJavaCode() {
			if (mJavaCode == null) {
				mJavaCode = printDecompiledClass();
			}
			return mJavaCode;
		}

		public ClassBlock getClassBlock() {
			return mClassBlock;
		}

		private String printDecompiledClass() {
			Writer stringWriter = new StringWriter();
			Writer printWriter = new PrintWriter(stringWriter);
			try {
				mClassBlock.write(printWriter);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return stringWriter.toString();
		}
	}
}
