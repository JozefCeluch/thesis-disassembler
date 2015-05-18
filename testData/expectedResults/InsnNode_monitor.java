public class InsnNode_monitor {

	public InsnNode_monitor() {
		super();
	}

	public synchronized int synchronizedMethod() {
		return 0;
	}

	public synchronized int synchronizedBlocInside() {
		int number = 0;
		synchronized ("abc") {
			number = 1;
			java.lang.System.out.println("inside synchronized block");
		}
		return number;
	}
}
