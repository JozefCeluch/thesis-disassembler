package com.thesis.file;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Printer;

public class DecompilerFieldVisitor extends FieldVisitor {
    public DecompilerFieldVisitor(FieldVisitor fieldVisitor, Printer printer) {
        super(Opcodes.ASM5, fieldVisitor);
    }
    //TODO
}
