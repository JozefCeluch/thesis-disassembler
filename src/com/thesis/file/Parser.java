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
import java.util.List;

public class Parser {

    Reader mReader;
//    JavaClass mClass;

    public Parser(String directory) {
        mReader = new Reader(directory);
    }

//    public JavaClass parseClassFile(String file) throws FileNotFoundException {
//
//        ClassParser parser = null;
//
//        parser = new ClassParser(mReader.openClassFile(file), file);
//        try {
//            mClass = parser.parse();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return mClass;
//    }

    public String parseClassFile(String file) throws FileNotFoundException {
        InputStream is = mReader.openClassFile(file);
        try {
            ClassReader classReader = new ClassReader(is);
//            ClassNode classNode = new ClassNode();
            StringWriter stringWriter = new StringWriter();
            ClassVisitor classVisitor = new DecompilerClassVisitor(new PrintWriter(stringWriter));
//            classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
//            System.out.println("access: " + classNode.access);
//            for (Object field : classNode.fields) {
//                System.out.print(((FieldNode) field).access);
//                System.out.print(" ");
//                System.out.print(((FieldNode) field).name);
//                System.out.print(" ");
//                System.out.println();
//            }
//
//            for (Object field : classNode.methods) {
//                System.out.print(((MethodNode) field).access);
//                System.out.print(" ");
//                System.out.print(((MethodNode) field).name);
//                System.out.print(" ");
//                System.out.print(((MethodNode) field).desc);
//                System.out.print(" ");
//                System.out.print(((MethodNode) field).maxLocals);
//                System.out.println();
//            }

        return stringWriter.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public void printCode() {
//        Method[] methods = mClass.getMethods();
//        for (int i = 0; i < methods.length; i++) {
//            System.out.println(methods[i]);
//
//            Code code = methods[i].getCode();
//            if (code != null) // Non-abstract method
//                System.out.println(code);
//        }
//    }

//    public JavaClass getJavaClass() {
//        return mClass;
//    }

    private String createClassName(int access, String name){
        return null;
    }
}
