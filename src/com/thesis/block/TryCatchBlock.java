package com.thesis.block;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class TryCatchBlock extends Block {

	private Block mTryBlock;
	private List<Block> mCatchBlocks;
	private Block mFinallyBlock;

	@Override
	public Block disassemble() {
		return this;
	}

	@Override
	public void write(Writer writer) throws IOException {

	}
}
