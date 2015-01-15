public class MethodInsnNode_invokevirtual {
public MethodInsnNode_invokevirtual() {
super();
}
public int firstPublicMethod(java.lang.String arg) {
java.lang.System.out.println(arg);
return arg.length();
}
public void secondPublicMethod() {
firstPublicMethod(new java.lang.String());
}
}
