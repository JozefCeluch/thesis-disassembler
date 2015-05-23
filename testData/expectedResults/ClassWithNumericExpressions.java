public class ClassWithNumericExpressions {

	public ClassWithNumericExpressions() {
		super();
	}

	private void createNewLocalVariables(int param1) {
		int v2 = 2;
		param1 = 0;
		boolean boolVar = false;
		int anotherIntVar = 6666666;
		int intVar = (anotherIntVar + 873) + ((anotherIntVar + param1) * v2);
		java.lang.String str = "some random string literal";
		int[] array = new int[14];
		param1 = - (param1 + 1);
		param1 += 1;
		param1 += 3000;
		param1 = param1 + 123456789;
		param1 = param1 / 3;
		param1 = param1 | 4;
		param1 = param1 | 5;
		param1 = param1 >> 6;
		param1 = param1 >>> 7;
		param1 = param1 << 9;
		float floatVar = 1F;
		double doubleVar = 2.0;
		param1 = array[3];
		v2 = v2++;
		param1 = (++param1) * (v2++);
		intVar += 1;
		intVar += 1;
		param1 += 5;
		param1 += -5;
		v2 = v2 ^ -1;
	}
}
