class TryCatchBlockNode {
TryCatchBlockNode() {
super();
}
void method() {
java.lang.String value;
int number;
java.lang.IndexOutOfBoundsException e;
java.lang.Object var4;
value = "4";
try {
try {
try {
number = java.lang.Integer.valueOf(value).intValue();
java.lang.System.out.println("no exception");
} catch (java.lang.NegativeArraySizeException e) {
number = (int) -6;
java.lang.System.out.println("NegativeArraySizeException caught");
}
java.lang.System.out.println("called finally");
} catch (java.lang.NumberFormatException e) {
number = -1;
java.lang.System.out.println("number exception caught");
java.lang.System.out.println("called finally");
} catch (java.lang.IndexOutOfBoundsException e) {
number = (int) -3;
java.lang.System.out.println("index out of bounds exception caught");
java.lang.System.out.println("called finally");
}
} catch (java.lang.Throwable var4) {
java.lang.System.out.println("called finally");
throw var4;
}
java.lang.System.out.println(new java.lang.StringBuilder().append("value is").append(number).toString());
}
}
