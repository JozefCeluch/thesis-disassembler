public class TypeInsnNode_new {

	public TypeInsnNode_new() {
		super();
	}

	void methodWithNewCall() {
		InnerClass firstObject = new InnerClass();
		InnerClass secondObject = new InnerClass(2);
		int firstNumber = 99;
		InnerClass thirdObject = new InnerClass(firstNumber);
		InnerClass fourthObject = new InnerClass(1, 2);
	}

	class InnerClass {

		private int intVar;

		public InnerClass() {
			intVar = 0;
		}

		public InnerClass(int number) {
			intVar = number;
		}

		public InnerClass(int numArg1, int numArg2){

		}

	}
}