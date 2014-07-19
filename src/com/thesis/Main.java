package com.thesis;

import com.thesis.file.Parser;
import org.apache.bcel.classfile.JavaClass;

public class Main {

    public static void main(String[] args) {
        System.out.println("line");
        Parser p = new Parser("tests");

        p.parseClassFile("Main.class");

        p.printCode();

    }


}
