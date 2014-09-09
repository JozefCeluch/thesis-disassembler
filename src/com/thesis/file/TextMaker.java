package com.thesis.file;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;

public class TextMaker extends Textifier {
    public TextMaker() {
        super(Opcodes.ASM5);
    }
    //region classes
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitSource(String file, String debug) {
//        super.visitSource(file, debug);
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        super.visitOuterClass(owner, name, desc);
    }

    @Override
    public Textifier visitClassAnnotation(String desc, boolean visible) {
        return super.visitClassAnnotation(desc, visible);
    }

    @Override
    public Printer visitClassTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return super.visitClassTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitClassAttribute(Attribute attr) {
        super.visitClassAttribute(attr);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public Textifier visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public Textifier visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//        return super.visitMethod(access, name, desc, signature, exceptions);
        return this;
    }

    @Override
    public void visitClassEnd() {
        super.visitClassEnd();
    }
    //endregion

    //region annotations
    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        super.visitEnum(name, desc, value);
    }

    @Override
    public Textifier visitAnnotation(String name, String desc) {
        return super.visitAnnotation(name, desc);
    }

    @Override
    public Textifier visitArray(String name) {
        return super.visitArray(name);
    }

    @Override
    public void visitAnnotationEnd() {
        super.visitAnnotationEnd();
    }
    //endregion

    //region fields
    @Override
    public Textifier visitFieldAnnotation(String desc, boolean visible) {
        return super.visitFieldAnnotation(desc, visible);
    }

    @Override
    public Printer visitFieldTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return super.visitFieldTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitFieldAttribute(Attribute attr) {
        super.visitFieldAttribute(attr);
    }

    @Override
    public void visitFieldEnd() {
        super.visitFieldEnd();
    }
    //endregion

    //region methods
    @Override
    public void visitParameter(String name, int access) {
        super.visitParameter(name, access);
    }

    @Override
    public Textifier visitAnnotationDefault() {
        return super.visitAnnotationDefault();
    }

    @Override
    public Textifier visitMethodAnnotation(String desc, boolean visible) {
        return super.visitMethodAnnotation(desc, visible);
    }

    @Override
    public Printer visitMethodTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return super.visitMethodTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public Textifier visitParameterAnnotation(int parameter, String desc, boolean visible) {
        return super.visitParameterAnnotation(parameter, desc, visible);
    }

    @Override
    public void visitMethodAttribute(Attribute attr) {
        super.visitMethodAttribute(attr);
    }

    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitInsn(int opcode) {
//        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
//        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
//        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
//        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
//        super.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
//        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
//        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
//        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
//        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
//        super.visitLdcInsn(cst);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
//        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
//        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
//        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
//        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public Printer visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
//        return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
        return this;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
//        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public Printer visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
//        return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
        return this;
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
//        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public Printer visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
//        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
        return this;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
//        super.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
//        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitMethodEnd() {
        super.visitMethodEnd();
    }
    //endregion

    //region common
    @Override
    public Textifier visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public Textifier visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        super.visitAttribute(attr);
    }
}
