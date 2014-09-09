package com.thesis.file;

import org.objectweb.asm.*;
import org.objectweb.asm.util.Printer;

import java.io.PrintWriter;

public class DecompilerClassVisitor extends ClassVisitor {

    private final Printer printer;
    private  final PrintWriter printWriter;

    public DecompilerClassVisitor(PrintWriter printWriter) {
        this(new TextMaker(), printWriter);
    }

    public DecompilerClassVisitor(Printer printer, PrintWriter printWriter) {
        super(Opcodes.ASM5);
        this.printer = printer;
        this.printWriter = printWriter;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        printer.visit(version, access, name, signature, superName, interfaces);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitSource(String source, String debug) {
        printer.visitSource(source, debug);
        super.visitSource(source, debug);
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        printer.visitOuterClass(owner, name, desc);
        super.visitOuterClass(owner, name, desc);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Printer p = printer.visitClassAnnotation(desc, visible);
        AnnotationVisitor av = cv == null ? null : cv.visitAnnotation(desc, visible);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        Printer p = printer.visitClassTypeAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor av = cv == null ? null : cv.visitTypeAnnotation(typeRef, typePath, desc, visible);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        printer.visitClassAttribute(attr);
        super.visitAttribute(attr);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        printer.visitInnerClass(name, outerName, innerName, access);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        Printer p = printer.visitField(access, name, desc, signature, value);
        FieldVisitor fv = cv == null ? null : cv.visitField(access, name, desc, signature, value);
        return new DecompilerFieldVisitor(fv, p);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        Printer p = printer.visitMethod(access, name, desc, signature, exceptions);
        MethodVisitor mv = cv == null ? null : cv.visitMethod(access, name, desc, signature, exceptions);
        return new DecompilerMethodVisitor(mv, p);
    }

    @Override
    public void visitEnd() {
        printer.visitClassEnd();
        if (printWriter != null) {
            printer.print(printWriter);
            printWriter.flush();
        }
        super.visitEnd();
    }
}
