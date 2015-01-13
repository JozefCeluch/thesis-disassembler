import java.lang.String;

public class MultiANewArrayInsnNode {

	private void method(){
		int[][][] threeDimenArray = new int[4][3][2];
		int dimen = 18;
		String[][][] stringsArray = new String[][][]{new String[][]{new String[]{"a", "b"}}};
		String[][][] stringsArray2 = new String[][][]{new String[3][4]};
		String[][][] stringsArray3 = new String[dimen + 3][dimen + 7][dimen + 12];
	}
}