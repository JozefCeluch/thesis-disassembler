public class LookupSwitchInsnNode {

	public LookupSwitchInsnNode() {
		super();
	}

	void simpleSwitch(int number) {
		switch (number) {
			case 1:
				java.lang.System.out.println("one");
				break;
			case 8:
				java.lang.System.out.println("eight");
				break;
			case 16:
				java.lang.System.out.println("sixteen");
				break;
			default:
				java.lang.System.out.println("default");
		}
		java.lang.System.out.println("AFTER SWITCH");
	}

	void stringSwitch(java.lang.String text) {
		java.lang.String var2 = text;
		int var3 = -1;
		switch (var2.hashCode()) {
			case 97440432:
				if (var2.equals("first")) {
					var3 = 0;
				}
			case -906279820:
				if (var2.equals("second")) {
					var3 = 1;
				}
			case 110331239:
				if (var2.equals("third")) {
					var3 = 2;
				}
			default:
				switch (var3) {
					case 0:
						java.lang.System.out.println("one");
						break;
					case 1:
						java.lang.System.out.println("eight");
						break;
					case 2:
						java.lang.System.out.println("sixteen");
						break;
					default:
						java.lang.System.out.println("default");
				}
				java.lang.System.out.println("AFTER SWITCH");
		}
	}
}
