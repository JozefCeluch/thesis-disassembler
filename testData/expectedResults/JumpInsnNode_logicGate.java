public class JumpInsnNode_logicGate {
public JumpInsnNode_logicGate() {
super();
}
boolean method() {
boolean bool;
boolean boolTwo;
int num1;
boolean booolThree;
bool = "i".isEmpty() && !"a".isEmpty();
boolTwo = !"s".isEmpty() || bool;
num1 = 4;
booolThree = (!(num1 > 4) || num1 <= 7) && (!"s".isEmpty() || bool);
return bool;
}
void methodTwo() {
int number;
int anotherNum;
anotherNum = 5;
if ("i".isEmpty() || anotherNum > 4) {
number = (int) 23;
java.lang.System.out.println("then branch");
} else {
java.lang.System.out.println("else branch");
}
}
}
