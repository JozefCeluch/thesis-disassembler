public class ClassWithBoolExpressions {
ClassWithBoolExpressions(){
}
public void simpleBoolExpressions(){
		boolean boolOne;
		boolean boolTwo;
		boolOne = true;
		boolTwo = false;
		boolOne = (!boolOne) && (!boolTwo);
	}

	public void iflExpression(){
		int intOne;
		int intTwo;
		int intThree;
		intOne = 5;
		intTwo = 45;
		if (intOne > intTwo) {
			intOne = 88;
		} else {
			intTwo = 99;
		}
		intThree = 55;
	}
}