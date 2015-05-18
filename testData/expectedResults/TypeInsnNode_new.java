public class TypeInsnNode_new {

	public TypeInsnNode_new() {
		super();
	}

	void methodWithNewCall() {
		InnerClass firstObject = new InnerClass(this);
		InnerClass secondObject = new InnerClass(this, 2);
		int firstNumber = 99;
		InnerClass thirdObject = new InnerClass(this, firstNumber);
		InnerClass fourthObject = new InnerClass(this, 1, 2);
	}

	class InnerClass {
		private int intVar;
		final  /* synthetic */ TypeInsnNode_new this$0;

		public InnerClass(TypeInsnNode_new this$0) {
			this.this$0 = this$0;
			super();
			this.intVar = 0;
		}

		public InnerClass(TypeInsnNode_new this$0, int number) {
			this.this$0 = this$0;
			super();
			this.intVar = number;
		}

		public InnerClass(TypeInsnNode_new this$0, int numArg1, int numArg2) {
			this.this$0 = this$0;
			super();
		}
	}
}
