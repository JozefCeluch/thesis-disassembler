public class JumpInsnNode_loops {
public JumpInsnNode_loops() {
super();
}
void method() {
int i;
i = 0;
while (i < 5) {
java.lang.System.out.println(new java.lang.StringBuilder().append("loop iteration ").append(i).toString());
i += 1;
}
while (true) {
java.lang.System.out.println("while true loop");
}
}
void anotherMethod() {
int i;
i = 0;
while (i < 10) {
if (i > 8) {
java.lang.System.out.println("conditional print");
}
java.lang.System.out.println("infinite loop iteration ");
i += 1;
}
}
void forEachLoop() {
java.util.List objects;
java.util.Iterator var2;
java.lang.Object o;
objects = new java.util.ArrayList();
var2 = objects.iterator();
while (var2.hasNext()) {
o = var2.next();
java.lang.System.out.println(o.toString());
}
}
void loopWithContinue() {
int i;
i = 0;
while (i < 10) {
i += 1;
if (i == 8) {
java.lang.System.out.println("call continue");
continue;
}
if (i > 2) {
java.lang.System.out.println("do nothing");
} else {
java.lang.System.out.println("call another continue");
continue;
}
java.lang.System.out.println("infinite loop iteration ");
}
}
void loopWithBreak() {
int i;
i = 0;
while (i < 10) {
i += 1;
if (i > 8) {
java.lang.System.out.println("call first break");
break;
} else {
if (i > 3) {
java.lang.System.out.println("infinite loop iteration ");
} else {
java.lang.System.out.println("call second break");
break;
}
java.lang.System.out.println("end of else branch");
}
}
}
}
