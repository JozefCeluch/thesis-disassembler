public class FieldInsnNode_static {
private static int staticInt;
public static java.lang.String staticStr;
public final static java.lang.String finalStr = "static string constant";
public FieldInsnNode_static() {
super();
}
void accessPrivateStatic() {
int localInt;
java.lang.String localStr;
java.lang.String anotherString;
localInt = FieldInsnNode_static.staticInt;
localStr = FieldInsnNode_static.staticStr;
anotherString = "static string constant";
}
static void <clinit>() {
FieldInsnNode_static.staticInt = 1;
FieldInsnNode_static.staticStr = "static string";
}
}
