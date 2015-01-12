public class ClassWithInnerClasses {
private int outerClassVariable;
private int otherIntVariable;
private java.lang.String anotherVariable;
public ClassWithInnerClasses() {
super();
}
static  /* synthetic */ int access$000(ClassWithInnerClasses x0) {
return x0.outerClassVariable;
}
static  /* synthetic */ int access$100(ClassWithInnerClasses x0) {
return x0.otherIntVariable;
}
static  /* synthetic */ java.lang.String access$200(ClassWithInnerClasses x0) {
return x0.anotherVariable;
}
public class OtherInnerClass {
final  /* synthetic */ ClassWithInnerClasses this$0;
public OtherInnerClass(ClassWithInnerClasses this$0) {
this.this$0 = this$0;
super();
}
}

class InnerClass {
final  /* synthetic */ ClassWithInnerClasses this$0;
public InnerClass(ClassWithInnerClasses arg0, java.lang.String s) {
this.this$0 = arg0;
super();
}
private int innerClassMethod() {
return ClassWithInnerClasses.access$000(this.this$0);
}
class VeryInnerClass {
final  /* synthetic */ InnerClass this$1;
public VeryInnerClass(InnerClass arg0, int a) {
this.this$1 = arg0;
super();
}
public int veryInnerClassMethod() {
return (ClassWithInnerClasses.access$000(this.this$1.this$0)) + (ClassWithInnerClasses.access$100(this.this$1.this$0));
}
private java.lang.String anotherVeryInnerClassMethod() {
return ClassWithInnerClasses.access$200(this.this$1.this$0);
}
}

}

}
