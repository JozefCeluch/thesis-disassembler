import java.lang.Object;
import java.util.ArrayList;

public class JumpInsnNode_loops {
	void forLoopWithooutVarInit(int i) {
		for (; i < 5; i += 1) {
			if (i == 3) {
				continue;
			}
			System.out.println("loop iteration " + i);
		}
		while (true) {
			System.out.println("while true loop");
		}
	}

	void completeForLoop() {
		for (int i = 0; i < 10; i++) {
			if (i > 8) {
				System.out.println("conditional print");
			}
			System.out.println("print");
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
			i++;
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

	void forLoopWithContinue() {
		for(int i = 0; i < 10;) {
			System.out.println("loop start");
			if (i == 4) {
				System.out.println("call continue");
				continue;
			} else {
				System.out.println("else branch");
				if (i > 6) {
					System.out.println("inner then branch");
					i++;
				} else {
					System.out.println("call inner continue");
					continue;
				}
				System.out.println("end of else branch");
			}
			System.out.println("loop end");
		}
	}
}