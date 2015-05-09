@ComplexAnnotation(value={@EmptyInterfaceAnnotation(name1={"sss", "ss"}, name2={"ds", "ssa", "dsa"}), @EmptyInterfaceAnnotation(name1={"ggg", "ggg"}, name2={})})
public class EmptyClassWithComplexAnnotation extends java.lang.ThreadLocal<java.lang.String> implements EmptyInterface<java.lang.String> {
	@ComplexAnnotation
	private java.lang.String annotatedField;

	public EmptyClassWithComplexAnnotation() {
		super();
		this.annotatedField = "sdd";
	}
}
