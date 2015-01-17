public class LookupSwitchInsnNode {

	void simpleSwitch(int number) {
		switch (number) {
			case 1:
				System.out.println("one");
				break;
			case 8:
				System.out.println("eight");
				break;
			case 16:
				System.out.println("sixteen");
				break;
			default:
				System.out.println("default");
		}
		;
		System.out.println("AFTER SWITCH");
	}

	void stringSwitch(String text) {
		switch (text) {
			case "first":
				System.out.println("one");
				break;
			case "second":
				System.out.println("eight");
				break;
			case "third":
				System.out.println("sixteen");
				break;
			default:
				System.out.println("default");
		}
		;
		System.out.println("AFTER SWITCH");
	}
}