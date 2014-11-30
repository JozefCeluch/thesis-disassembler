public class InsnNode_i2x {
public InsnNode_i2x() {
}
private byte intToByte() {
int x;
byte b;
byte b2;
x = (int) -45;
b = (byte) x;
b2 = -5;
return (byte) (b & b2);
}
}
