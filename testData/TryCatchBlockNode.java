import java.lang.*;
import java.lang.IndexOutOfBoundsException;
import java.lang.Integer;
import java.lang.NumberFormatException;
import java.lang.String;

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
		} finally {
			System.out.println("called finally");
		}
		System.out.println("value is" + number);
	}

	void onlyFinally() {
		try {
			int number = Integer.valueOf("3");
		} finally {
			System.out.println("called finally");
		}
	}
}