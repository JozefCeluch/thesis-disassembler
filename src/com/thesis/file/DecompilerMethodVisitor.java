package com.thesis.file;

import org.objectweb.asm.*;
import org.objectweb.asm.util.Printer;

public class DecompilerMethodVisitor extends MethodVisitor {
    private final Printer printer;

    public DecompilerMethodVisitor(MethodVisitor methodVisitor, Printer printer) {
        super(Opcodes.ASM5, methodVisitor);
        this.printer = printer;
    }

    @Override
    public void visitParameter(String name, int access) {
        printer.visitParameter(name, access);
        super.visitParameter(name, access);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return super.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Printer p = printer.visitMethodAnnotation(desc, visible);
        AnnotationVisitor av = mv == null ? null : mv.visitAnnotation(desc, visible);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        Printer p = printer.visitMethodTypeAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor av = mv == null ? null : mv.visitTypeAnnotation(typeRef, typePath, desc, visible);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        Printer p = printer.visitParameterAnnotation(parameter, desc, visible);
        AnnotationVisitor av = mv == null ? null : mv.visitParameterAnnotation(parameter, desc, visible);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        printer.visitMethodAttribute(attr);
        super.visitAttribute(attr);
    }

    @Override
    public void visitCode() {
        printer.visitCode();
        super.visitCode();
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        printer.visitFrame(type, nLocal, local, nStack, stack);
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitInsn(int opcode) {
        printer.visitInsn(opcode);
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        printer.visitIntInsn(opcode, operand);
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        printer.visitVarInsn(opcode, var);
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        printer.visitTypeInsn(opcode, type);
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        printer.visitFieldInsn(opcode, owner, name, desc);
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        printer.visitMethodInsn(opcode, owner, name, desc, itf);
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        printer.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        printer.visitJumpInsn(opcode, label);
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        printer.visitLabel(label);
        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        printer.visitLdcInsn(cst);
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        printer.visitIincInsn(var, increment);
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        printer.visitTableSwitchInsn(min, max, dflt, labels);
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        printer.visitLookupSwitchInsn(dflt, keys, labels);
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        printer.visitMultiANewArrayInsn(desc, dims);
        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        Printer p = printer.visitInsnAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor av = mv == null ? null : mv.visitInsnAnnotation(typeRef, typePath, desc, visible);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        printer.visitTryCatchBlock(start, end, handler, type);
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        Printer p = printer.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor av = mv == null ? null : mv.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        printer.visitLocalVariable(name, desc, signature, start, end, index);
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        Printer p = printer.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
        AnnotationVisitor av = mv == null ? null : mv.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
        return new DecompilerAnnotationVisitor(av, p);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        printer.visitLineNumber(line, start);
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        printer.visitMaxs(maxStack, maxLocals);
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        printer.visitMethodEnd();
        super.visitEnd();
    }
}
