public class TableSwitchInsnNode {

	void simpleSwitch(int number){
		switch (number) {
			case 1:
				System.out.println("one");
				break;
			case 2:
				System.out.println("two");
				break;
			case 3:
				System.out.println("three");
				break;
			default:
				System.out.println("default");
		};
		System.out.println("AFTER SWITCH");

		number = 999;
	}

	void moreComplexswitchWithHoles(int number){
		switch (number) {
			case 1:
				if(number * 45 > 12) {
					System.out.println("if is true");
				} else {
					System.out.println("if is false");
				}
				//without this statement it does not decompile well
				// because the GOTO in then branch points to the end of switch
				System.out.println("after if");
				break;
			case 3:
				System.out.println(number > 4 ? "something" : "three");
			case 4:
				System.out.println("four");
				break;
			default:
				System.out.println("default");
		};
		System.out.println("AFTER SWITCH");
	}
}