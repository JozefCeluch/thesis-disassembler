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
}