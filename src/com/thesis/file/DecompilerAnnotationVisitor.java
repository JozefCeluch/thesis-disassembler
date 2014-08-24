package com.thesis.file;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Printer;

public class DecompilerAnnotationVisitor extends AnnotationVisitor {

    private final Printer printer;

    public DecompilerAnnotationVisitor(AnnotationVisitor annotationVisitor, Printer printer) {
        super(Opcodes.ASM5, annotationVisitor);
        this.printer = printer;
    }

    @Override
    public void visit(String name, Object value) {
        printer.visit(name, value);
        super.visit(name, value);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        printer.visitEnum(name, desc, value);
        super.visitEnum(name, desc, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        Printer p = printer.visitAnnotation(name, desc);
        AnnotationVisitor av = this.av == null ? null : this.av.visitAnnotation(name, desc);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        Printer p = printer.visitArray(name);
        AnnotationVisitor av = this.av == null ? null : this.av.visitArray(name);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public void visitEnd() {
        printer.visitAnnotationEnd();
        super.visitEnd();
    }
}
