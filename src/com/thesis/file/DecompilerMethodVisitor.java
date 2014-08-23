package com.thesis.file;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Printer;

public class DecompilerMethodVisitor extends MethodVisitor {
    public DecompilerMethodVisitor(MethodVisitor methodVisitor, Printer printer) {
        super(Opcodes.ASM5, methodVisitor);
    }
    //TODO
}
