import java.io.PrintStream;
import java.lang.System;

public class FieldInsnNode_static {
	private static int staticInt = 1;
	public static String staticStr = "static string";
	public static final String finalStr = "static string constant";

	void accessPrivateStatic() {
		PrintStream stream = System.out;
		int localInt = staticInt;
		String localStr = staticStr;
		String anotherString = finalStr;
	}
}