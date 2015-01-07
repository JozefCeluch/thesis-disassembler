public class MethodInsnNode_invokestatic {
public MethodInsnNode_invokestatic() {
super();
}
public static int firstStaticMethod(java.lang.String argument) {
return argument.length();
}
public static void secondStaticMethod() {
MethodInsnNode_invokestatic.firstStaticMethod(new java.lang.String());
}
}
