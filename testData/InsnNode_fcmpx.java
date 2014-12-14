package test;
public class InsnNode_fcmpx {
	private boolean compareFloatGreaterThan() {
		float float1 = 3.0F;
		float float2 = 4.0F;
		if (float1 > float2) {
			float1 = 99.0F;
		} else {
			float2 = 88.0F;
		}
		return true;
	}

	private boolean compareFloatLessEqual() {
		float float1 = 3.0F;
		float float2 = 4.0F;
		if (float1 <= float2) {
			float1 = 99.0F;
		} else {
			float2 = 88.0F;
		}
		return true;
	}

	private boolean compareFloatNotEqual() {
		float float1 = 3.0F;
		float float2 = 4.0F;
		if (float1 != float2) {
			float1 = 99.0F;
		} else {
			float2 = 88.0F;
		}
		return true;
	}
}
