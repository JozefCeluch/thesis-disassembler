import java.lang.Comparable;

public class ClassWithMethods {
	public static <T> java.util.Map<java.lang.Integer, java.lang.Float>[] staticWithGeneric(T anotherGenericArg) {
		return null;
	}

	public static int staticWithSimpleArgs(int firstInt, int secondInt, @SimpleAnnotation int thirdInt) {
		return secondInt;
	}

	public void voidMethodNoArgs() {
	}

	private void voidMethodWithOneArg(boolean arg0) {
	}

	private void voidMethodWithMoreArgs(@SimpleAnnotation short[] arg0, @SimpleAnnotation int arg1, java.lang.String arg2, float arg3, @SimpleAnnotation java.lang.String arg4) {
	}

	private void voidMethodWithMorePrimitiveArgs(int arg0, boolean arg1, short arg2) {
	}

	private void voidMethodWithVarArgs(int... arg0) {
	}

	private int intMethodWithExceptions() throws java.lang.NullPointerException, java.lang.Exception {
		return 0;
	}

	public <T extends Comparable> T genericMethod(T genericArg) {
		return genericArg;
	}

	public java.util.Map<java.lang.Integer, java.lang.Float>[] crazyReturn() {
		return null;
	}

	public <T> java.util.Map<java.lang.Integer, java.lang.Float>[] usesGeneric(T anotherGenericArg) {
		return null;
	}

	public int calculations(int arg0, int arg1, int arg2) {
		return arg1;
	}
}
