package com.thesis.block;

import com.thesis.file.Parser;
import com.thesis.common.Util;
import org.objectweb.asm.tree.InnerClassNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;

public class InnerClassBlock extends Block {

	InnerClassNode mInnerClassNode;
	String mOuterClassName;

	public InnerClassBlock(InnerClassNode innerClassNode, String outerClassName, Block parent) {
		mInnerClassNode = innerClassNode;
		mOuterClassName = outerClassName;
		mParent = parent;
	}

	@Override
	public Block disassemble() {
		if (mOuterClassName.equals(mInnerClassNode.outerName)) {
			appendInnerClassNode(mInnerClassNode.name);
		}
		return this;
	}

	private void appendInnerClassNode(String name) {
		Parser p = new Parser("testData/"); //todo folder

		text.add(p.parseClassFile(name + ".class", getParent())); //todo make extension optional
		text.add(NL);
	}

	@Override
	public void write(Writer writer) throws IOException {
		printList(writer, text);
	}
}
