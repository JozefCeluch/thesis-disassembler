package test;
@interface RepeatableAnnotation {
SimpleAnnotation[] value() default {};
}