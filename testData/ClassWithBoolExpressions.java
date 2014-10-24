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
}