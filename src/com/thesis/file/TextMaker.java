package com.thesis.file;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;

public class TextMaker extends Printer {

    Printer printer;

    /**
     * Constructs a new {@link org.objectweb.asm.util.Printer}.
     *
     * @param api
     */
    protected TextMaker(int api) {
        super(api);
        printer = new Textifier();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

    }

    @Override
    public void visitSource(String file, String debug) {

    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {

    }

    @Override
    public Printer visitClassAnnotation(String desc, boolean visible) {
        return null;
    }

    @Override
    public void visitClassAttribute(Attribute attr) {

    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {

    }

    @Override
    public Printer visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }

    @Override
    public Printer visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return null;
    }

    @Override
    public void visitClassEnd() {

    }

    @Override
    public void visit(String name, Object value) {

    }

    @Override
    public void visitEnum(String name, String desc, String value) {

    }

    @Override
    public Printer visitAnnotation(String name, String desc) {
        return null;
    }

    @Override
    public Printer visitArray(String name) {
        return null;
    }

    @Override
    public void visitAnnotationEnd() {

    }

    @Override
    public Printer visitFieldAnnotation(String desc, boolean visible) {
        return null;
    }

    @Override
    public void visitFieldAttribute(Attribute attr) {

    }

    @Override
    public void visitFieldEnd() {

    }

    @Override
    public Printer visitAnnotationDefault() {
        return null;
    }

    @Override
    public Printer visitMethodAnnotation(String desc, boolean visible) {
        return null;
    }

    @Override
    public Printer visitParameterAnnotation(int parameter, String desc, boolean visible) {
        return null;
    }

    @Override
    public void visitMethodAttribute(Attribute attr) {

    }

    @Override
    public void visitCode() {

    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {

    }

    @Override
    public void visitInsn(int opcode) {

    }

    @Override
    public void visitIntInsn(int opcode, int operand) {

    }

    @Override
    public void visitVarInsn(int opcode, int var) {

    }

    @Override
    public void visitTypeInsn(int opcode, String type) {

    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {

    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {

    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {

    }

    @Override
    public void visitLabel(Label label) {

    }

    @Override
    public void visitLdcInsn(Object cst) {

    }

    @Override
    public void visitIincInsn(int var, int increment) {

    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {

    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {

    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {

    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {

    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {

    }

    @Override
    public void visitLineNumber(int line, Label start) {

    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {

    }

    @Override
    public void visitMethodEnd() {

    }
}
