import java.lang.Override;

public class ClassWithAnonymousClasses {

	private int globalNum;
	private A classA = new A();

	public void classMethod() {
		final int[] number = {3};
		SomeInterface anonObj = new SomeInterface() {
			private int num1 = number[0];
			@Override
			public int interfaceMethod() {
				return globalNum;
			}
		};
	}

	public interface SomeInterface {
		int interfaceMethod();
	}

	private class A {
		private int privateIntA = globalNum;
	}

	private class B {
		private int privateIntB = ClassWithAnonymousClasses.this.classA.privateIntA;
	}
}