package com.thesis.file;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Printer;

public class DecompilerAnnotationVisitor extends AnnotationVisitor {
    public DecompilerAnnotationVisitor(AnnotationVisitor annotationVisitor, Printer printer) {
        super(Opcodes.ASM5, annotationVisitor);
    }
    //TODO
}
