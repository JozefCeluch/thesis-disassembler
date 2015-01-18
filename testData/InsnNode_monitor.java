import java.lang.String;

public class InsnNode_monitor {

	public synchronized int synchronizedMethod(){
		return 0;
	}

	public synchronized int synchronizedBlocInside(){
		int number = 0;
		synchronized ("abc") {
			number = 1;
			System.out.println("inside synchronized block");
		}
		return number;
	}
}