import java.lang.Byte;
import java.lang.Integer;
import java.lang.Object;

public class TypeInsnNode_instanceof {

	public boolean notInstance() {
		return this instanceof Object;
	}

	public void isInstance() {
		int localInt = 1;
		boolean bool = this instanceof Object && localInt < Byte.MIN_VALUE;
		if ("" instanceof Object) {
			localInt = 2;
		} else {
			localInt = -1;
		}
	}

}