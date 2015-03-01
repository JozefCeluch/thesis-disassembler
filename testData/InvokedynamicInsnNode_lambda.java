import java.lang.Integer;
import java.lang.Runnable;
import java.lang.String;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InvokedynamicInsnNode_lambda {

	public void lambdaRunnable() {
		Runnable r = () -> System.out.println("Hello world two!");
		r.run();
	}

	public List<Person> sortWithLambda(List<Person> people) {
		Collections.sort(people, (p1, p2) -> p1.getAge().compareTo(p2.getAge()));
		return people;
	}

	public static class Person {
		private String name;
		private Integer age;

		public String getName() {
			return name;
		}

		public Integer getAge() {
			return age;
		}
	}
}