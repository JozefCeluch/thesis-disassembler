public class MultiANewArrayInsnNode {

	public MultiANewArrayInsnNode() {
		super();
	}

	private void method() {
		int[][][] threeDimenArray = new int[4][3][2];
		int dimen = 18;
		java.lang.String[][][] stringsArray = new java.lang.String[][][]{new java.lang.String[][]{new java.lang.String[]{"a", "b"}}};
		java.lang.String[][][] stringsArray2 = new java.lang.String[][][]{new java.lang.String[3][4]};
		java.lang.String[][][] stringsArray3 = new java.lang.String[dimen + 3][dimen + 7][dimen + 12];
	}
}
