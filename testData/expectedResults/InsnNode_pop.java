public class InsnNode_pop {

	public InsnNode_pop() {
		super();
	}

	public int returnInt() {
		return 1;
	}

	public long returnLong() {
		return 9223372036854775807L;
	}

	public int popInt() {
		returnInt();
		return 0;
	}

	public int popLong() {
		returnLong();
		return 0;
	}
}
