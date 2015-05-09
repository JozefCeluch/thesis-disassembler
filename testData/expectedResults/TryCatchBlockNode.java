class TryCatchBlockNode {

	TryCatchBlockNode() {
		super();
	}

	void innerTryCatch() {
		java.lang.String value;
		int number;
		java.lang.RuntimeException e;
		java.lang.Object var4;
		value = "4";
		try {
			try {
				number = java.lang.Integer.valueOf(value).intValue();
				java.lang.System.out.println("no exception");
			} catch (java.lang.NegativeArraySizeException e) {
				number = -6;
				java.lang.System.out.println("NegativeArraySizeException caught");
			}
		} catch (java.lang.NumberFormatException e) {
			number = -1;
			java.lang.System.out.println("number exception caught");
		} catch (java.lang.IndexOutOfBoundsException e) {
			number = -3;
			java.lang.System.out.println("index out of bounds exception caught");
		} catch (java.lang.NullPointerException | java.lang.ArithmeticException e) {
			number = 5;
			java.lang.System.out.println("multicatch block");
		} finally {
			java.lang.System.out.println("called finally");
		}
		java.lang.System.out.println(new java.lang.StringBuilder().append("value is").append(number).toString());
	}

	void onlyFinally() {
		int number;
		java.lang.Object var2;
		number = 0;
		try {
			number = java.lang.Integer.valueOf("3").intValue();
		} finally {
			java.lang.System.out.println(new java.lang.StringBuilder().append("called finally with ").append(number).toString());
		}
	}
}
