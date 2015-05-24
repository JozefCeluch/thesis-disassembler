package com.thesis.file;

import com.thesis.block.Block;
import com.thesis.block.ClassBlock;
import com.thesis.exception.DecompilerException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;

/**
 * The class responsible for initiating the decompilation process
 * <p>
 * Works as a singleton in order to facilitate decompilation of inner classes
 */
public class Disassembler {

    private Reader mReader;
	private static Disassembler mDisassembler;

	/**
	 * Creates an instance of this class
	 * <p>
	 * The instance is stored as static field so subsequent calls to {@link Disassembler#getInstance()}
	 * return this instance
	 * @param directory where the classfiles are stored
	 * @return initialized Disassembler
	 */
	public static Disassembler createInstance(String directory) {
		mDisassembler = new Disassembler(directory);
		return mDisassembler;
	}

	/**
	 *
	 * @return initialized instance of Disassembler, or null if {@link Disassembler#createInstance(String)} was not called before
	 */
	public static Disassembler getInstance() {
		return mDisassembler;
	}

    private Disassembler(String directory) {
        mReader = new Reader(directory);
    }

	/**
	 * Decompiles the file, should be used from outside
	 * @param file name of the file to decompile
	 * @return decompiled class in the encapsulating object
	 * @throws DecompilerException or its subclass, in case of a problem
	 */
    public DecompilationResult decompileClassFile(String file) throws DecompilerException {
		ClassReader classReader = getClassReader(file);
		ClassBlock classBlock = disassembleClass(classReader, null);
		return new DecompilationResult(classBlock);
    }

	/**
	 * Decompiles the inner class file
	 * @param file name of the file to decompile
	 * @param parent enclosing class
	 * @return instance of decompiled class that is used internally
	 * @throws DecompilerException or its subclass, in case of a problem
	 */
	public ClassBlock decompileInnerClass(String file, Block parent) throws DecompilerException {
		file = file.endsWith(".class") ? file : file + ".class";
		return disassembleClass(getClassReader(file), parent);
	}

	private ClassBlock disassembleClass(ClassReader classReader, Block parent) {
		Writer bytecodeStringWriter = new StringWriter();
		ClassVisitor classVisitor = new TraceClassVisitor(new PrintWriter(bytecodeStringWriter));
		classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

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

	/**
	 * Holds the result of the decompilation process
	 * <p>
	 * Holds the instance of ClassBlock of the decompiled class and provides methods to
	 * retrieve strings with Java source code and bytecode in ASM format
	 */
	@SuppressWarnings("unused")
	public static class DecompilationResult {
		private ClassBlock mClassBlock;

		private String mJavaCode = null;
		private String mBytecode = null;

		DecompilationResult(ClassBlock classBlock) {
			mClassBlock = classBlock;
		}

		/**
		 * Convenience method to get the bytecode
		 * @return string representation of class bytecode in ASM format
		 */
		public String getBytecode() {
			if (mBytecode == null) {
				mBytecode = mClassBlock.getBytecode();
			}
			return mBytecode;
		}

		/**
		 * Convenience method to get the generated Java code
		 * @return string representation of the decompiled Java code
		 */
		public String getJavaCode() {
			if (mJavaCode == null) {
				mJavaCode = printDecompiledClass();
			}
			return mJavaCode;
		}

		/**
		 * Returns the complete decompiled ClassBlock
		 * @return decompiled object
		 */
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
