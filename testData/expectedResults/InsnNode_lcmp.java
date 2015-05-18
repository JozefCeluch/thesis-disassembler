public class InsnNode_lcmp {

	public InsnNode_lcmp() {
		super();
	}

	private boolean compareLongGreaterEqual() {
		long long1 = 3L;
		long long2 = 4L;
		if (long1 >= long2) {
			long1 = 99L;
			return 1;
		}
		long2 = 88L;
		return 0;
	}

	private boolean compareLongEqual() {
		long long1 = 3L;
		long long2 = 4L;
		if (long1 == long2) {
			long1 = 99L;
			return 1;
		}
		long2 = 88L;
		return 0;
	}

	private boolean compareLongLessThan() {
		long long1 = 3L;
		long long2 = 4L;
		if (long1 < long2) {
			long1 = 99L;
			return 1;
		}
		long2 = 88L;
		return 0;
	}
}
