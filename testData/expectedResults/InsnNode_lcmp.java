public class InsnNode_lcmp {
public InsnNode_lcmp() {
}
private boolean compareLongGreaterEqual() {
long long1;
long long2;
long1 = 3;
long2 = 4;
if (long1 >= long2) {
long1 = 99;
return 1;
}
long2 = 88;
return 0;
}
private boolean compareLongEqual() {
long long1;
long long2;
long1 = 3;
long2 = 4;
if (long1 == long2) {
long1 = 99;
return 1;
}
long2 = 88;
return 0;
}
private boolean compareLongLessThan() {
long long1;
long long2;
long1 = 3;
long2 = 4;
if (long1 < long2) {
long1 = 99;
return 1;
}
long2 = 88;
return 0;
}
}
