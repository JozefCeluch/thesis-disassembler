package com.thesis.file;

import org.objectweb.asm.tree.InnerClassNode;

import java.io.FileNotFoundException;
import java.util.List;

public class InnerClassBlock extends Block {

	InnerClassNode mInnerClassNode;
	String mOuterClassName;

	public InnerClassBlock(InnerClassNode innerClassNode, String outerClassName) {
		mInnerClassNode = innerClassNode;
		mOuterClassName = outerClassName;
	}

	public List<Object> disassemble() {
		if (mOuterClassName.equals(mInnerClassNode.outerName)) {
			appendInnerClassNode(mInnerClassNode.name);
		}
		return text;
	}

	private void appendInnerClassNode(String name) {
		Parser p = new Parser("testData/"); //todo folder
		try {
			text.add(p.parseClassFile(name + ".class")); //todo make extension optional
			text.add(NEW_LINE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
