public class InsnNode_arraylength {
public InsnNode_arraylength() {
super();
}
int simpleArrayLength() {
double[] doubles;
doubles = new double[5];
return ((new int[]{1, 2, 4}.length) + (doubles.length)) + (getArray().length);
}
int[] getArray() {
return new int[1];
}
}
