public @interface EmptyInterfaceAnnotation {
java.lang.String[] name1();
java.lang.String[] name2();
EmptyEnum[] en() default {};
}