public class InsnNode_dcmpx {

	public InsnNode_dcmpx() {
		super();
	}

	private boolean compareDoubleGreaterThan() {
		double double1;
		double double2;
		double1 = 3.0;
		double2 = 4.0;
		if (double1 > double2) {
			double1 = 99.0;
		} else {
			double2 = 88.0;
		}
		return 0;
	}

	private boolean compareDoubleLessEqual() {
		double double1;
		double double2;
		double1 = 3.0;
		double2 = 4.0;
		if (double1 <= double2) {
			double1 = 99.0;
		} else {
			double2 = 88.0;
		}
		return 0;
	}
}
