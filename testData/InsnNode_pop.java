public class InsnNode_pop {
	public int returnInt(){
		return 1;
	}

	public long returnLong(){
		return Long.MAX_VALUE;
	}

	public int popInt(){
		returnInt();
		return 0;
	}

	public int popLong(){
		returnLong();
		return 0;
	}

}