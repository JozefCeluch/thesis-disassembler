public final enum EmptyEnum extends java.lang.Enum<EmptyEnum> {
public final static enum EmptyEnum A;
public final static enum EmptyEnum B;
private final static  /* synthetic */ EmptyEnum[] $VALUES;
public static EmptyEnum[] values() {
return EmptyEnum.$VALUES.clone();
}
public static EmptyEnum valueOf(java.lang.String name) {
return java.lang.Enum.valueOf(EmptyEnum.class, name);
}
private EmptyEnum(java.lang.String arg0, int arg1) {
super(arg0, arg1);
}
static void <clinit>() {
EmptyEnum.A = new EmptyEnum("A", 0);
EmptyEnum.B = new EmptyEnum("B", 1);
EmptyEnum.$VALUES = new EmptyEnum[]{EmptyEnum.A, EmptyEnum.B};
}
}
