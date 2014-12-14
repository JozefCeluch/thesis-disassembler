package test;
public class ClassWithNumericExpressions {
public ClassWithNumericExpressions() {
}
private void createNewLocalVariables(int param1) {
int intVar;
int anotherIntVar;
boolean boolVar;
int v2;
double doubleVar;
float floatVar;
java.lang.String str;
int[] array;
v2 = 2;
param1 = 0;
boolVar = false;
anotherIntVar = 6666666;
intVar = anotherIntVar + 873 + (anotherIntVar + param1) * v2;
str = "some random string literal";
array = new int[14];
param1 = param1 + 1;
param1 += 1;
param1 += 3000;
param1 += 123456789;
param1 /= 3;
param1 |= 4;
param1 = param1 | 5;
param1 = param1 >> 6;
param1 = param1 >>> 7;
param1 = param1 << 9;
floatVar = 1;
doubleVar = 2.0;
param1 = array[3];
v2 = v2++;
param1 = ++param1 * v2++;
intVar++;
++intVar;
param1 += 5;
param1 += -5;
v2 = ~v2;
}
}
