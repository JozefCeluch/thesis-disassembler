public class FieldInsnNode_field {
private int intField;
public boolean boolField;
protected long longField;
public java.lang.String stringField;
java.lang.String unusedField;
public FieldInsnNode_field() {
boolField = false;
stringField = "string literal";
}
void assignFields() {
intField = 100;
boolField = true;
longField = 43;
stringField = "another literal";
}

}