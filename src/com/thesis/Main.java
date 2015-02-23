package com.thesis;

import com.thesis.file.Parser;

import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) {
		Parser p = Parser.createInstance("tests/example");

		p.parseClassFile("Atom.class");

//        p.printCode();

	}


}
