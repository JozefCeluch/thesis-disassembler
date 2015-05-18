public class JumpInsnNode_logicGate {

	public JumpInsnNode_logicGate() {
		super();
	}

	void methodThree() {
		boolean bool = false;
		int num1 = 5;
		boolean booolThree = num1 > 4 || num1 <= 7 && "s".isEmpty() && bool;
	}

	boolean methodOne() {
		boolean bool = "i".isEmpty() && !"a".isEmpty();
		boolean boolTwo = !"s".isEmpty() || bool;
		int num1 = 4;
		boolean booolThree = num1 <= 4 && num1 <= 7 || !"s".isEmpty() && bool && num1 >= 3 && num1 <= 6;
		return bool;
	}

	void methodTwo() {
		int anotherNum = 5;
		if ("i".isEmpty() || anotherNum > 4) {
			int number = 23;
			java.lang.System.out.println("then branch");
		} else {
			java.lang.System.out.println("else branch");
		}
	}
}
