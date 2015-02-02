public class JumpInsnNode_logicGate {

	void methodThree() {
		boolean bool = false;
		int num1 = 5;
		boolean booolThree = (num1 > 4 && num1 <= 7) || ("s".isEmpty() && bool);
	}

	boolean methodOne() {
		boolean bool = "i".isEmpty() && !"a".isEmpty();
		boolean boolTwo = !"s".isEmpty() || bool;
		int num1 = 4;
		boolean booolThree = (!(num1 > 4) && num1 <= 7) || ("s".isEmpty() && bool);
		return bool;
	}

	void methodTwo() {
		int number;
		int anotherNum = 5;
		if ("i".isEmpty() || anotherNum > 4) {
			number = 23;
			System.out.println("then branch");
		} else {
			System.out.println("else branch");
		}
	}
}