package com.thesis;

import com.thesis.file.Parser;
import org.apache.bcel.classfile.JavaClass;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        Parser p = new Parser("tests/example");

        try {
            p.parseClassFile("Atom.class");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        p.printCode();

    }


}
