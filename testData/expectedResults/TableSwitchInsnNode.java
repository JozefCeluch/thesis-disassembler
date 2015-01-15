public class TableSwitchInsnNode {
public TableSwitchInsnNode() {
super();
}
void method(int number) {
switch (number) {
case 1:
java.lang.System.out.println("one");
break;
case 2:
java.lang.System.out.println("two");
break;
case 3:
java.lang.System.out.println("three");
break;
default:
java.lang.System.out.println("default");
}
java.lang.System.out.println("AFTER SWITCH");
number = (int) 999;
}
}
