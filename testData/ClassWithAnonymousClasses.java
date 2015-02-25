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
				SomeInterface innerAnonObj = new SomeInterface() {
					public int interfaceMethod() {
						class XA extends A.AA {
							public XA() {
								super();
								innerA = 76;
							}
						}
						A.AA xaaObject = new XA();
						return 99;
					}
				};
				return innerAnonObj.interfaceMethod();
			}
		};
	}

	public interface SomeInterface {
		int interfaceMethod();
	}

	public static class A {
		private static int privateIntA = 88;

		public static int getPrivateA() {
			return privateIntA;
		}
		public static class AA {
			protected static int innerA = 77;
			public static int getInnerInt() {
				return AA.innerA;
			}

		}
	}

	private class B {
		private int privateIntB = ClassWithAnonymousClasses.this.classA.privateIntA;
		private int anotherInt= ClassWithAnonymousClasses.A.AA.getInnerInt();
	}
}