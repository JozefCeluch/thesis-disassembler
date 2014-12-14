package test;
public class InsnNode_lcmp {
	private boolean compareLongGreaterEqual() {
		long long1 = 3;
		long long2 = 4;
		if (long1 >= long2) {
			long1 = 99;
			return true;
		} else {
			long2 = 88;
			return false;
		}
	}

	private boolean compareLongEqual() {
		long long1 = 3;
		long long2 = 4;
		if (long1 == long2) {
			long1 = 99;
			return true;
		} else {
			long2 = 88;
			return false;
		}
	}

	private boolean compareLongLessThan() {
		long long1 = 3;
		long long2 = 4;
		if (long1 < long2) {
			long1 = 99;
			return true;
		} else {
			long2 = 88;
			return false;
		}
	}
}