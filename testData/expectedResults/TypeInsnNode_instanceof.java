public class TypeInsnNode_instanceof {

	public TypeInsnNode_instanceof() {
		super();
	}

	public boolean notInstance() {
		return this instanceof java.lang.Object;
	}

	public void isInstance() {
		int localInt;
		boolean bool;
		localInt = 1;
		bool = this instanceof java.lang.Object && localInt < -128;
		if ("" instanceof java.lang.Object) {
			localInt = 2;
		} else {
			localInt = -1;
		}
	}
}
