package test;
public class InsnNode_dcmpx {
	private boolean compareDoubleGreaterThan() {
		double double1 = 3.0;
		double double2 = 4.0;
		if (double1 > double2) {
			double1 = 99.0;
		} else {
			double2 = 88.0;
		}
		return false;
	}

	private boolean compareDoubleLessEqual() {
		double double1 = 3.0;
		double double2 = 4.0;
		if (double1 <= double2) {
			double1 = 99.0;
		} else {
			double2 = 88.0;
		}
		return false;
	}
}
