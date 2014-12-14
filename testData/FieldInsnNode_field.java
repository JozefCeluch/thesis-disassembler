public class FieldInsnNode_field {

	private int intField;
	public boolean boolField = false;
	protected long longField;
	public String stringField = "string literal";
	String unusedField;

	void assignFields(){
		InnerClass c = new InnerClass();
		intField = c.number;
		intField = 100;
		boolField = true;
		longField = 43;
		stringField = "another literal";
	}

	private class InnerClass {
		public int number;
	}

}