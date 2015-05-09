public class MultiANewArrayInsnNode {

	public MultiANewArrayInsnNode() {
		super();
	}

	private void method() {
		int[][][] threeDimenArray;
		int dimen;
		java.lang.String[][][] stringsArray;
		java.lang.String[][][] stringsArray2;
		java.lang.String[][][] stringsArray3;
		threeDimenArray = new int[4][3][2];
		dimen = 18;
		stringsArray = new java.lang.String[][][]{new java.lang.String[][]{new java.lang.String[]{"a", "b"}}};
		stringsArray2 = new java.lang.String[][][]{new java.lang.String[3][4]};
		stringsArray3 = new java.lang.String[dimen + 3][dimen + 7][dimen + 12];
	}
}
