public class FieldInsnNode_field {
private int intField;
public boolean boolField;
protected long longField;
public java.lang.String stringField;
java.lang.String unusedField;
public FieldInsnNode_field() {
this.boolField = false;
this.stringField = "string literal";
}
void assignFields() {
InnerClass c;
int localInt;
c = null;
this.intField = c.number;
this.intField = 100;
this.boolField = true;
this.longField = 43L;
this.stringField = "another literal";
localInt = this.intField;
}
class InnerClass {
public int number;
final  /* synthetic */ FieldsnNode_field this$0;
private InnerClass(FieldInsnNode_field arg0) {
this.this$0 = arg0;
}
 /* synthetic */ InnerClass(FieldInsnNode_field x0, 1 x1) {
}
}

}
