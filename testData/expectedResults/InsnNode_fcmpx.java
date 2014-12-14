public class InsnNode_fcmpx {
public InsnNode_fcmpx() {
super();
}
private boolean compareFloatGreaterThan() {
float float1;
float float2;
float1 = 3.0F;
float2 = 4.0F;
if (float1 > float2) {
float1 = 99.0F;
} else {
float2 = 88.0F;
}
return 1;
}
private boolean compareFloatLessEqual() {
float float1;
float float2;
float1 = 3.0F;
float2 = 4.0F;
if (float1 <= float2) {
float1 = 99.0F;
} else {
float2 = 88.0F;
}
return 1;
}
private boolean compareFloatNotEqual() {
float float1;
float float2;
float1 = 3.0F;
float2 = 4.0F;
if (float1 != float2) {
float1 = 99.0F;
} else {
float2 = 88.0F;
}
return 1;
}
}
