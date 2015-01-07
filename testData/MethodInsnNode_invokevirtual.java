public class MethodInsnNode_invokevirtual {
	public int firstPublicMethod(String arg) {
		return arg.length();
	}

	public void secondPublicMethod() {
		firstPublicMethod(new String());
	}
}