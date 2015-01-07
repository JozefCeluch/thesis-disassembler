public class MethodInsnNode_invokestatic {

	public static int firstStaticMethod(java.lang.String argument) {
		return argument.length();
	}

	public static void secondStaticMethod() {
		firstStaticMethod(new java.lang.String());
	}
}