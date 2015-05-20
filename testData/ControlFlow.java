public class ControlFlow {
	public static int foo(int i, int j) {
		while (true) {
			try {
				while (i < j)
					i = j++ / i;
			} catch (RuntimeException re){
				i = 10;
				continue;
			}
			break;
		}
		return j;
	}

	public static void main(String[] args) {
		System.out.println(foo(1, 2));
	}
}