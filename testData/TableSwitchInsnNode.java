public class TableSwitchInsnNode {

	void method(int number){
		switch (number) {
			case 1: {
				System.out.println("one");
				break;
			}
			case 2: {
				System.out.println("two");
				break;
			}
			case 3: {
				System.out.println("three");
				break;
			}
			default: {
				System.out.println("default");
			}
		};
		System.out.println("AFTER SWITCH");

		number = 999;
	}
}