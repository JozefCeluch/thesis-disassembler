public class InsnNode_i2x {
	private byte intToByte() {
		int x = -45;
		byte b = (byte) x;
		byte b2 = (byte) -1;
		return (byte) (b & b2);
	}
}