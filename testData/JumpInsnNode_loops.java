import java.lang.Object;
import java.util.ArrayList;

public class JumpInsnNode_loops {
	void method() {
		int i;
		for (i = 0; i < 5; i += 1) {
			System.out.println("loop iteration " + i);
		}
		while (true) {
			System.out.println("while true loop");
		}
	}

	void anotherMethod() {
		for (int i = 0; i < 10; i++) {
			if (i > 8) {
				System.out.println("conditional print");
			}
			System.out.println("infinite loop iteration ");
		}
	}

	void forEachLoop() {
		java.util.List objects = new ArrayList<>();
		for(Object o : objects) {
			System.out.println(o.toString());
		}
	}

	void loopWithContinue() {
		int i = 0;
		while (i < 10) {
			i++;
			if (i == 8) {
				System.out.println("call continue");
				continue;
			} else {
				if (i > 2) {
					System.out.println("do nothing");
				} else {
					System.out.println("call another continue");
					continue;
				}
			}
			System.out.println("infinite loop iteration ");
		}
	}

	void loopWithBreak() {
		int i = 0;;
		while (i < 10) {
			i++;
			if (i > 8) {
				System.out.println("call first break");
				break;
			} else  {
				if (i > 3) {
					System.out.println("infinite loop iteration ");
				} else {
					System.out.println("call second break");
					break;
				}
				System.out.println("end of else branch");
			}
		}
	}

	void doLoop() {
		int i = 0;
		do {
			System.out.println("val of i =" + i);
			if (i == 2) {
				System.out.println("TWO");
			} else {
				System.out.println("NUMBER");
			}
			i++;
		} while (i < 5 && i > 10);
	}
}