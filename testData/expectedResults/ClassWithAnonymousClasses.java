public class ClassWithAnonymousClasses {
	private int globalNum;
	private A classA;

	public ClassWithAnonymousClasses() {
		super();
		this.classA = new A(this, null);
	}

	static  /* synthetic */ int access$100(ClassWithAnonymousClasses x0) {
		return x0.globalNum;
	}

	static  /* synthetic */ A access$200(ClassWithAnonymousClasses x0) {
		return x0.classA;
	}

	public void classMethod() {
		int[] number;
		SomeInterface anonObj;
		number = new int[]{3};
		anonObj = new $1(this, number);
	}

	public interface SomeInterface {
		public abstract int interfaceMethod();
	}

	class B {
		final  /* synthetic */ ClassWithAnonymousClasses $0;
		private int privateIntB;

		private B(ClassWithAnonymousClasses arg0) {
			this.$0 = arg0;
			super();
			this.privateIntB = A.access$300(ClassWithAnonymousClasses.access$200(this.$0));
		}
	}

	class A {
		final  /* synthetic */ ClassWithAnonymousClasses $0;
		private int privateIntA;

		private A(ClassWithAnonymousClasses arg0) {
			this.$0 = arg0;
			super();
			this.privateIntA = ClassWithAnonymousClasses.access$100(this.this$0);
		}

		/* synthetic */ A(ClassWithAnonymousClasses x0, $1 x1) {
			this(x0);
		}

		static  /* synthetic */ int access$300(A x0) {
			return x0.privateIntA;
		}
	}

	class $1 implements SomeInterface {
		final  /* synthetic */ int[] val$number;
		final  /* synthetic */ ClassWithAnonymousClasses $0;
		private int num1;

		$1(ClassWithAnonymousClasses $0, int[] arg1) {
			this.$0 = $0;
			this.val$number = arg1;
			super();
			this.num1 = this.val$number[0];
		}

		public int interfaceMethod() {
			return ClassWithAnonymousClasses.access$100(this.$0);
		}
	}

}
