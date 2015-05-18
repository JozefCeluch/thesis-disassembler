public class JumpInsnNode_conditions {

	public JumpInsnNode_conditions() {
		super();
	}

	public int simpleBoolExpressions() {
		int intOne = 6;
		int intTwo = 8728;
		boolean boolOne = (intOne >= intTwo) & (intTwo > 3 && intOne < 67);
		intOne = intTwo > 34 ? 99 : 1;
		return intTwo >= 34 ? 99 : intTwo > 34 ? 0 : 1;
	}

	public void ifExpression() {
		int intThree;
		int intOne = 5;
		int intTwo = 45;
		if ((intOne > intTwo) & (intOne > 433) && intTwo > 46) {
			boolean boolOne = intOne > 56;
			intThree = 324;
		} else {
			if (intTwo >= 354) {
				intTwo = 999;
			}
			intTwo = 99;
		}
		if (intOne > 1111) {
			intTwo = 11;
		} else {
			if (intOne == 2222) {
				intTwo = 22;
			} else {
				intTwo = 33;
			}
		}
		intThree = 55;
	}

	private void nullConditionals() {
		boolean bool = "string" != null;
		if (this == null) {
			bool = this != null;
		}
	}
}
