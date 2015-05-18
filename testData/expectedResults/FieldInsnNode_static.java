public class FieldInsnNode_static {
	private static int staticInt;
	public static java.lang.String staticStr;
	public final static java.lang.String finalStr = "static string constant";

	public FieldInsnNode_static() {
		super();
	}

	void accessPrivateStatic() {
		java.io.PrintStream stream = java.lang.System.out;
		int localInt = FieldInsnNode_static.staticInt;
		java.lang.String localStr = FieldInsnNode_static.staticStr;
		java.lang.String anotherString = "static string constant";
	}

	static void <clinit>() {
		FieldInsnNode_static.staticInt = 1;
		FieldInsnNode_static.staticStr = "static string";
	}
}
