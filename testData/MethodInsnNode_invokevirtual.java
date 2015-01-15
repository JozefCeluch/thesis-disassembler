public class MethodInsnNode_invokevirtual {
	public int firstPublicMethod(String arg) {
		System.out.println(arg);
		return arg.length();
	}

	public void secondPublicMethod() {
		firstPublicMethod(new String());
	}
}