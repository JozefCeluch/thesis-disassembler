public class TypeInsnNode_anewarray {

	public TypeInsnNode_anewarray() {
		super();
	}

	int[] simpleTypeArray() {
		int[] intArray = new int[]{1, 21, 3};
		long[] longs = new long[]{111111111111111111L, 222222222222222222L, 33333333333333333L};
		return intArray;
	}

	java.lang.String[] objectArray() {
		java.lang.String[] strArray = new java.lang.String[]{"a", "b", "c"};
		int position = 13;
		strArray[position + 82432] = "r";
		return strArray;
	}
}
