public class InsnNode_i2x {

	public InsnNode_i2x() {
		super();
	}

	private byte intToByte() {
		int x = -45;
		byte b = (byte) x;
		byte b2 = -5;
		return (byte) (b & b2);
	}
}
