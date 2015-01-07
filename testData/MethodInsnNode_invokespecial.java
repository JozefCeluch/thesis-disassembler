import java.lang.String;

public class MethodInsnNode_invokespecial {

	private void createObjects() {
		String arg = new String();
		MethodInsnNode_invokespecial arg2 = new MethodInsnNode_invokespecial();
		arg2.anotherPrivateMethod(arg);
		anotherPrivateMethod(arg);
	}

	private String anotherPrivateMethod(String string){
		return string;
	}

	public void callPrivateMethod(String string) {
		anotherPrivateMethod(string);
	}
}