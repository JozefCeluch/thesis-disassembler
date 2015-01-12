public class ClassWithInnerClasses {
	private int outerClassVariable;
	private int otherIntVariable;
	private String anotherVariable;

	private class InnerClass {
		public InnerClass(String s) {
		}

		private int innerClassMethod() {
			return outerClassVariable;
		}

		private class VeryInnerClass {
			public VeryInnerClass(int a) {
			}

			public int veryInnerClassMethod(){
				return outerClassVariable + otherIntVariable;
			}

			private String anotherVeryInnerClassMethod(){
				return anotherVariable;
			}
		}
	}

	public class OtherInnerClass {
	}
}