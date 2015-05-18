import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.lang.*;
import java.lang.ArithmeticException;
import java.lang.IndexOutOfBoundsException;
import java.lang.Integer;
import java.lang.NullPointerException;
import java.lang.NumberFormatException;
import java.lang.String;
import java.lang.Throwable;

class TryCatchBlockNode {

	void innerTryCatch() {
		String value = "4";
		int number;
		try {
			try {
				number = Integer.valueOf(value);
				System.out.println("no exception");
			} catch (java.lang.NegativeArraySizeException e) {
				number = -6;
				System.out.println("NegativeArraySizeException caught");
			}
		} catch (NumberFormatException e) {
			number = -1;
			System.out.println("number exception caught");
		} catch (IndexOutOfBoundsException e) {
			number = -3;
			System.out.println("index out of bounds exception caught");
		}  catch (NullPointerException | ArithmeticException e) {
			number = 5;
			System.out.println("multicatch block");
		} finally {
			System.out.println("called finally");
		}
		System.out.println("value is" + number);
	}

	void onlyFinally() {
		int number = 0;
		try {
			number = Integer.valueOf("3");
		} finally {
			System.out.println("called finally with " + number);
		}
	}

	int finallyWithReturn() {
		String number = "1";
		try {
			return Integer.valueOf(number);
		} catch (NumberFormatException e) {
			return 0;
		} finally {
			return -1;
		}
	}

	void catchWithThrow() throws IOException {
		String a = "a";
		try {
			int l = a.length();
		} catch (NullPointerException e) {
			a = "caught";
			throw new IOException(e);
		} finally {
			a = "finally";
		}
	}

	void simplePrintFile() throws IOException {
		FileInputStream input = new FileInputStream("file.txt");
		try {
			int data = input.read();
			while(data != -1){
				System.out.print((char) data);
				data = input.read();
			}
		} finally {
			if (input == null) {
				System.out.println("INPUT IS NULL");
			}
			if (input != null) {
				input.close();
			}
			else {
				System.out.println("ELSE BRANCH");
			}
		}
	}

	void tryWithResourcesLikePrintFile() throws IOException {
		FileInputStream input = new FileInputStream("file.txt");
		Throwable exception = null;
		try {
			int data = input.read();
			while(data != -1){
				System.out.print((char) data);
				data = input.read();
			}
		} catch (Throwable throwable) {
			exception = throwable;
			throw throwable;
		} finally {
			if(input != null) {
				if(exception != null) {
					try {
						input.close();
					} catch (Throwable innerException) {
						exception.addSuppressed(innerException);
					}
				} else {
					input.close();
				}
			}
		}
	}

	void tryWithResourcesPrintFile() throws IOException {
		try(FileInputStream input = new FileInputStream("file.txt")) {

			int data = input.read();
			while(data != -1){
				System.out.print((char) data);
				data = input.read();
			}
		}
	}
}