package com.thesis.file;

import com.sun.org.apache.bcel.internal.util.ClassPath;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Parser {

    Reader mReader;
    JavaClass mClass;

    public Parser(String directory) {
        mReader = new Reader(directory);
    }

    public JavaClass parseClassFile(String file) throws FileNotFoundException {

        ClassParser parser = null;

        parser = new ClassParser(mReader.openClassFile(file), file);
        try {
            mClass = parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mClass;
    }

    public void printCode() {
        Method[] methods = mClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            System.out.println(methods[i]);

            Code code = methods[i].getCode();
            if (code != null) // Non-abstract method
                System.out.println(code);
        }
    }
}
