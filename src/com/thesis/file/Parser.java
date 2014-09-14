package com.thesis.file;

import jdk.internal.org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.ClassReader;
import org.apache.bcel.classfile.JavaClass;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private Reader mReader;

    public Parser(String directory) {
        mReader = new Reader(directory);
    }

    public String parseClassFile(String file) throws FileNotFoundException {
        InputStream is = mReader.openClassFile(file);
        try {
            ClassReader classReader = new ClassReader(is);
            ClassNode classNode = new ClassNode();
            StringWriter stringWriter = new StringWriter();
            classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			Disassembler disassembler = new Disassembler(new PrintWriter(stringWriter));
			disassembler.disassembleClass(classNode);
			disassembler.print();
        return stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
			return null;
        }
    }
}
