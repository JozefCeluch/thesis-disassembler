class TryCatchBlockNode {

	TryCatchBlockNode() {
		super();
	}

	void innerTryCatch() {
		int number;
		java.lang.String value = "4";
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
		int number = 0;
		try {
			number = java.lang.Integer.valueOf("3").intValue();
		} finally {
			java.lang.System.out.println(new java.lang.StringBuilder().append("called finally with ").append(number).toString());
		}
	}

	int finallyWithReturn() {
		java.lang.String number = "1";
		try {
			int var2 = java.lang.Integer.valueOf(number).intValue();
		} catch (java.lang.NumberFormatException e) {
			int var3 = 0;
		} finally {
			return -1;
		}
	}

	void catchWithThrow() throws java.io.IOException {
		java.lang.String a = "a";
		try {
			int var2 = a.length();
		} catch (java.lang.NullPointerException e) {
			a = "caught";
			throw new java.io.IOException(e);
		} finally {
			a = "finally";
		}
	}

	void simplePrintFile() throws java.io.IOException {
		java.io.FileInputStream input = new java.io.FileInputStream("file.txt");
		try {
			int data = input.read();
			while (data != -1) {
				java.lang.System.out.print((char) data);
				data = input.read();
			}
		} finally {
			if (input == null) {
				java.lang.System.out.println("INPUT IS NULL");
			}
			if (input != null) {
				input.close();
			} else {
				java.lang.System.out.println("ELSE BRANCH");
			}
		}
	}

	void tryWithResourcesLikePrintFile() throws java.io.IOException {
		java.io.FileInputStream input = new java.io.FileInputStream("file.txt");
		java.lang.Throwable exception = null;
		try {
			int data = input.read();
			while (data != -1) {
				java.lang.System.out.print((char) data);
				data = input.read();
			}
		} catch (java.lang.Throwable throwable) {
			exception = throwable;
			throw throwable;
		} finally {
			if (input != null) {
				if (exception != null) {
					try {
						input.close();
					} catch (java.lang.Throwable innerException) {
						exception.addSuppressed(innerException);
					}
				} else {
					input.close();
				}
			}
		}
	}

	void tryWithResourcesPrintFile() throws java.io.IOException {
		java.io.FileInputStream input = new java.io.FileInputStream("file.txt");
		java.lang.Object var2 = null;
		try {
			int data = input.read();
			while (data != -1) {
				java.lang.System.out.print((char) data);
				data = input.read();
			}
		} catch (java.lang.Throwable var3) {
			var2 = java.lang.Object;
			throw var3;
		} finally {
			if (input != null) {
				if (var2 != null) {
					try {
						input.close();
					} catch (java.lang.Throwable var5) {
						var2.addSuppressed(var5);
					}
				} else {
					input.close();
				}
			}
		}
	}
}
