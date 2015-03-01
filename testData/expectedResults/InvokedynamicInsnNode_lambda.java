public class InvokedynamicInsnNode_lambda {
public InvokedynamicInsnNode_lambda() {
super();
}
public void lambdaRunnable() {
java.lang.Runnable r;
r = java.lang.invoke.LambdaMetafactory.metafactory(/*stacked automatically by the VM*/ java.lang.invoke.MethodHandles.lookup(), "run", java.lang.invoke.MethodType.methodType(java.lang.Runnable.class), java.lang.invoke.MethodType.methodType(void.class), java.lang.invoke.MethodHandles.lookup().findStatic(InvokedynamicInsnNode_lambda.class, "lambda$lambdaRunnable$0", java.lang.invoke.MethodType.methodType(void.class)), java.lang.invoke.MethodType.methodType(void.class));
r.run();
}
public java.util.List<InvokedynamicInsnNode_lambda$Person> sortWithLambda(java.util.List<InvokedynamicInsnNode_lambda$Person> people) {
java.util.Collections.sort(people, java.lang.invoke.LambdaMetafactory.metafactory(/*stacked automatically by the VM*/ java.lang.invoke.MethodHandles.lookup(), "compare", java.lang.invoke.MethodType.methodType(java.util.Comparator.class), java.lang.invoke.MethodType.methodType(int.class), java.lang.invoke.MethodHandles.lookup().findStatic(InvokedynamicInsnNode_lambda.class, "lambda$sortWithLambda$1", java.lang.invoke.MethodType.methodType(int.class, Person.class, Person.class)), java.lang.invoke.MethodType.methodType(int.class)));
return people;
}
private static  /* synthetic */ int lambda$sortWithLambda$1(Person p1, Person p2) {
return p1.getAge().compareTo(p2.getAge());
}
private static  /* synthetic */ void lambda$lambdaRunnable$0() {
java.lang.System.out.println("Hello world two!");
}
public class Person {
private java.lang.String name;
private java.lang.Integer age;
public Person() {
super();
}
public java.lang.String getName() {
return this.name;
}
public java.lang.Integer getAge() {
return this.age;
}
}
}
