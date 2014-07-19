package com.thesis.file;

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

    public void parseClassFile(String file){

        ClassParser parser = null;
        try {
            parser = new ClassParser(mReader.openFile(file), file);
            mClass =  parser.parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printCode() {
        Method[] methods = mClass.getMethods();
        for(int i=0; i < methods.length; i++) {
            System.out.println(methods[i]);

            Code code = methods[i].getCode();
            if(code != null) // Non-abstract method
                System.out.println(code);
        }
    }
}
