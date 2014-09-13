import java.util.Map;

public class ClassWithFields {

	int number;

	private long longNumber;

	protected float floatNumber;

	private int[] numberArray;

	private int[][] numberArray2D;

	private java.lang.String string;

	private java.lang.String[] stringArray;

	private java.util.Map<java.lang.Integer, java.lang.Float>[] mapArray;

	public ClassWithFields() {
	}

	public ClassWithFields(int arg0, long arg1, float arg2, int[] arg3, int[][] arg4, String arg5, String[] arg6) {
		this.number = arg0;
		this.longNumber = arg1;
		this.floatNumber = arg2;
		this.numberArray = arg3;
		this.numberArray2D = arg4;
		this.string = arg5;
		this.stringArray = arg6;
	}
}
