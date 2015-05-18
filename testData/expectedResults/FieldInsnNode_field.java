public class FieldInsnNode_field {
	private int intField;
	public boolean boolField;
	protected long longField;
	public java.lang.String stringField;
	java.lang.String unusedField;

	public FieldInsnNode_field() {
		super();
		this.boolField = false;
		this.stringField = "string literal";
	}

	void assignFields() {
		InnerClass c = new InnerClass(this, null);
		this.intField = c.number;
		this.intField = 100;
		this.boolField = true;
		this.longField = 43L;
		this.stringField = "another literal";
		int localInt = this.intField;
	}

	 /* synthetic */ class FieldInsnNode_field$1 {
	}

	class InnerClass {
		public int number;
		final  /* synthetic */ FieldInsnNode_field this$0;

		private InnerClass(FieldInsnNode_field arg0) {
			this.this$0 = arg0;
			super();
		}

		 /* synthetic */ InnerClass(FieldInsnNode_field x0, FieldInsnNode_field$1 x1) {
			this(x0);
		}
	}
}
