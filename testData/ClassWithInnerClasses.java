import java.lang.Long;

public class ClassWithInnerClasses {
	private int outerClassVariable;
	private int otherIntVariable;
	private String anotherVariable;
	private static final OtherInnerClass a = new OtherInnerClass();

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

	public static class OtherInnerClass {
	}
}