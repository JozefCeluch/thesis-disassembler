@java.lang.annotation.Repeatable(value=RepeatableAnnotation.class)
@interface SimpleAnnotation {

	public abstract int value() default 1;
}
