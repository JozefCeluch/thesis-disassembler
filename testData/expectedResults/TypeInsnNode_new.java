public class TypeInsnNode_new {
public TypeInsnNode_new() {
super();
}
void methodWithNewCall() {
InnerClass firstObject;
InnerClass secondObject;
int firstNumber;
InnerClass thirdObject;
InnerClass fourthObject;
firstObject = new InnerClass(this);
secondObject = new InnerClass(this, 2);
firstNumber = (int) 99;
thirdObject = new InnerClass(this, firstNumber);
fourthObject = new InnerClass(this, 1, 2);
}
class InnerClass {
private int intVar;
final  /* synthetic */ TypeInsnNode_new this$0;
public InnerClass(TypeInsnNode_new this$0) {
this.this$0 = this$0;
super();
this.intVar = 0;
}
public InnerClass(TypeInsnNode_new this$0, int number) {
this.this$0 = this$0;
super();
this.intVar = number;
}
public InnerClass(TypeInsnNode_new this$0, int numArg1, int numArg2) {
this.this$0 = this$0;
super();
}
}
}
