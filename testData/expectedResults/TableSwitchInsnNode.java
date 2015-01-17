public class TableSwitchInsnNode {
public TableSwitchInsnNode() {
super();
}
void simpleSwitch(int number) {
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
void moreComplexswitchWithHoles(int number) {
switch (number) {
case 1:
if (number * 45 > 12) {
java.lang.System.out.println("if is true");
} else {
java.lang.System.out.println("if is false");
}
java.lang.System.out.println("after if");
break;
case 3:
java.lang.System.out.println(number > 4 ? "something" : "three");
case 4:
java.lang.System.out.println("four");
break;
default:
java.lang.System.out.println("default");
}
java.lang.System.out.println("AFTER SWITCH");
}
}
