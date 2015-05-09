public class ClassWithAnonymousClasses {
	private int globalNum;
	private A classA;

	public ClassWithAnonymousClasses() {
		super();
		this.classA = new A();
	}

	public void classMethod() {
		int[] number;
		SomeInterface anonObj;
		number = new int[]{3};
		anonObj = new ClassWithAnonymousClasses$1(this, number);
	}

	static  /* synthetic */ A access$000(ClassWithAnonymousClasses x0) {
		return x0.classA;
	}

	class B {
		private int privateIntB;
		private int anotherInt;
		final  /* synthetic */ ClassWithAnonymousClasses this$0;

		private B(ClassWithAnonymousClasses arg0) {
			this.this$0 = arg0;
			super();
			this.privateIntB = ClassWithAnonymousClasses.A.access$100();
			this.anotherInt = ClassWithAnonymousClasses.A.AA.getInnerInt();
		}
	}

	public class A {
		private static int privateIntA;

		public A() {
			super();
		}

		public static int getPrivateA() {
			return A.privateIntA;
		}

		static  /* synthetic */ int access$100() {
			return A.privateIntA;
		}

		static void <clinit>() {
			A.privateIntA = 88;
		}

		public class AA {
			protected static int innerA;

			public AA() {
				super();
			}

			public static int getInnerInt() {
				return AA.innerA;
			}

			static void <clinit>() {
				AA.innerA = 77;
			}
		}
	}

	public interface SomeInterface {

		public abstract int interfaceMethod();
	}

	class ClassWithAnonymousClasses$1 implements SomeInterface {
		private int num1;
		final  /* synthetic */ int[] val$number;
		final  /* synthetic */ ClassWithAnonymousClasses this$0;

		ClassWithAnonymousClasses$1(ClassWithAnonymousClasses this$0, int[] arg1) {
			this.this$0 = this$0;
			this.val$number = arg1;
			super();
			this.num1 = this.val$number[0];
		}

		public int interfaceMethod() {
			SomeInterface innerAnonObj;
			innerAnonObj = new ClassWithAnonymousClasses$1$1(this);
			return innerAnonObj.interfaceMethod();
		}

		class ClassWithAnonymousClasses$1$1 implements SomeInterface {
			final  /* synthetic */ ClassWithAnonymousClasses$1 this$1;

			ClassWithAnonymousClasses$1$1(ClassWithAnonymousClasses$1 this$1) {
				this.this$1 = this$1;
				super();
			}

			public int interfaceMethod() {
				AA xaaObject;
				xaaObject = new ClassWithAnonymousClasses$1$1$1XA(this);
				return 99;
			}

			class ClassWithAnonymousClasses$1$1$1XA extends AA  {
				final  /* synthetic */ ClassWithAnonymousClasses$1$1 this$2;

				public ClassWithAnonymousClasses$1$1$1XA(ClassWithAnonymousClasses$1$1 this$2) {
					this.this$2 = this$2;
					super();
					ClassWithAnonymousClasses$1$1$1XA.innerA = 76;
				}
			}
		}
	}
}
