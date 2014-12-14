package test;
public @interface ComplexAnnotation {
	public EmptyInterfaceAnnotation[] value() default {};
}