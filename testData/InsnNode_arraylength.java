public class InsnNode_arraylength {
	int simpleArrayLength() {
		double[] doubles = new double[5];
		return new int[]{1, 2, 4}.length + doubles.length + getArray().length;
	}

	int[] getArray(){
		return new int[1];
	}
}