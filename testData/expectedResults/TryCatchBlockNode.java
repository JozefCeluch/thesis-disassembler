class TryCatchBlockNode {
TryCatchBlockNode() {
super();
}
void method() {
java.lang.String value;
int number;
value = "4";
try {
try {
number = java.lang.Integer.valueOf(value).intValue();
java.lang.System.out.println("no exception");
} catch (java.lang.NegativeArraySizeException e) {
number = (int) -6;
java.lang.System.out.println("NegativeArraySizeException caught");
}
} catch (java.lang.NumberFormatException e) {
number = -1;
java.lang.System.out.println("number exception caught");
} catch (java.lang.IndexOutOfBoundsException e) {
number = (int) -3;
java.lang.System.out.println("index out of bounds exception caught");
} finally {
java.lang.System.out.println("called finally");
}
java.lang.System.out.println(new java.lang.StringBuilder().append("value is").append(number).toString());
}
}
