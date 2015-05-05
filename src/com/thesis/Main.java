package com.thesis;

import com.thesis.file.Disassembler;

public class Main {

	public static void main(String[] args) {
		Disassembler p = Disassembler.createInstance("tests/example");

		p.decompileClassFile("Atom.class");

//        p.printCode();

	}


}
