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
		appendInnerClassNode(mInnerClassNode.name);
		return this;
	}

	private void appendInnerClassNode(String name) {
		text.add(Parser.getInstance().parseClassFile(name + ".class", getParent())); //todo make extension optional
	}

	@Override
	public void write(Writer writer) throws IOException {
		printList(writer, text);
	}
}
