public @interface EmptyInterfaceAnnotation {
public abstract java.lang.String[] name1();
public abstract java.lang.String[] name2();
public abstract EmptyEnum[] en() default {EmptyEnum.A};
}