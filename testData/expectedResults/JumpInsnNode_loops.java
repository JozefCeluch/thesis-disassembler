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
}
