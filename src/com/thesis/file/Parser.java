package com.thesis.file;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Parser {

    Reader mReader;

    public Parser(String directory) {
        mReader = new Reader(directory);
    }

    public JavaClass parseClassFile(String file){

        ClassParser parser = null;
        try {
            parser = new ClassParser(mReader.openFile(file), file);
            return  parser.parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
