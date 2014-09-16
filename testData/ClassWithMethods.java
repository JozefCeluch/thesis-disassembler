public class ClassWithMethods {
public ClassWithMethods() {
}
public void voidMethodNoArgs() {
}
private void voidMethodWithOneArg(boolean arg0) {
}
private void voidMethodWithMoreArgs(short[] arg0, int arg1, java.lang.String arg2, float arg3, java.lang.String arg4) {
}
private void voidMethodWithMorePrimitiveArgs(int arg0, boolean arg1, short arg2) {
}
private void voidMethodWithVarArgs(int... arg0) {
}
private int intMethodWithExceptions() throws java.lang.NullPointerException, java.lang.Exception {
return 0;
}
public <T> T genericMethod(T arg0) {
return arg0;
}
public java.util.Map<java.lang.Integer, java.lang.Float>[] crazyReturn() {
return null;
}
public <T> java.util.Map<java.lang.Integer, java.lang.Float>[] usesGeneric(T arg0) {
return null;
}
}
