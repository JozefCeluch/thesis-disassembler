package com.thesis.file;

import org.objectweb.asm.*;
import org.objectweb.asm.util.Printer;

public class DecompilerFieldVisitor extends FieldVisitor {
    private final Printer printer;

    public DecompilerFieldVisitor(FieldVisitor fieldVisitor, Printer printer) {
        super(Opcodes.ASM5, fieldVisitor);
        this.printer = printer;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Printer p = printer.visitFieldAnnotation(desc, visible);
        AnnotationVisitor av = fv == null ? null : fv.visitAnnotation(desc, visible);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        Printer p = printer.visitFieldTypeAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor av = fv == null ? null : fv.visitTypeAnnotation(typeRef, typePath, desc, visible);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        printer.visitFieldAttribute(attr);
        super.visitAttribute(attr);
    }

    @Override
    public void visitEnd() {
        printer.visitFieldEnd();
        super.visitEnd();
    }
}
