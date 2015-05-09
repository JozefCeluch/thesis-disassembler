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

//	void standardPrintFile() throws IOException {
//		FileInputStream input = null;
//
//		try {
//			input = new FileInputStream("file.txt");
//
//			int data = input.read();
//			while(data != -1){
//				System.out.print((char) data);
//				data = input.read();
//			}
//		} finally {
//			if(input != null){
//				try {
//					input.close();
//				} catch (IOException e) {
//
//				}
//			}
//		}
//	}

//	void tryWithResourcesPrintFile() throws IOException {
//		try(FileInputStream input = new FileInputStream("file.txt")) {
//
//			int data = input.read();
//			while(data != -1){
//				System.out.print((char) data);
//				data = input.read();
//			}
//		}
//	}
}