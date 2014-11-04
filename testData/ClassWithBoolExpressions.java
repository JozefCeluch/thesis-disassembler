public class ClassWithBoolExpressions {
ClassWithBoolExpressions(){
}
public void simpleBoolExpressions(){
		boolean boolOne;
		int intOne;
		int intTwo;
		intOne = 6;
		intTwo = 8728;
		boolOne = intOne >= intTwo & (intTwo > 3 && intOne < 67);
	}

public void iflExpression(){
		int intOne;
		int intTwo;
		int intThree;
		boolean boolOne;
		intOne = 5;
		intTwo = 45;
		if (intOne > intTwo & intOne > 433 && intTwo > 46) {
			boolOne = intOne > 56;
			intThree = 324;
		} else {
			if (intTwo >= 354) {
				intTwo = 999;
			} else {
				intTwo = 888;
			}
			intTwo = 99;
		}
		intThree = 55;
	}
}