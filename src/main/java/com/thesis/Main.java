package com.thesis;

import com.thesis.exception.DecompilerException;
import com.thesis.file.Disassembler;

public class Main {

	public static void main(String[] args) {
		Disassembler p = Disassembler.createInstance("tests/example");

		try {
			p.decompileClassFile("Atom.class");
		} catch (DecompilerException e) {
			e.printStackTrace();
		}

//        p.printCode();

	}


}
