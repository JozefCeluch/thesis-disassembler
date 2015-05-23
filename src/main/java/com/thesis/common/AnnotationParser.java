package com.thesis.common;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.List;

/**
 * A class that provides parsing of annotations and returns them in a single String
 */
public class AnnotationParser {

	private StringBuffer buf;

	public AnnotationParser() {
		buf = new StringBuffer();
	}

	/**
	 * Converts the annotation list to Java source string
	 * @param annotations list of annotations
	 * @param separator separating character between anotations
	 * @return textual representation of annotations
	 */
	public String getAnnotations(List annotations, String separator) {
		return getAnnotations(annotations, null, separator);
	}

	/**
	 * Converts the annotation list to Java source string
	 * @param annotations list of annotations
	 * @param prefix a string that is added before each annotation (e.g. tabs)
	 * @param suffix a string that is added after each annotation (e.g. separator)
	 * @return textual representation of annotations
	 */
	public String getAnnotations(List annotations, String prefix, String suffix) {
		if (annotations == null) return "";

		buf.setLength(0);
		for (Object annotation : annotations) {
			if (prefix != null) buf.append(prefix);
			addAnnotationNode(((AnnotationNode)annotation).desc, ((AnnotationNode)annotation).values);
			if (suffix != null) buf.append(suffix);
		}
		return buf.toString();
	}

	/**
	 * Converts the default value of annotation interface method
	 * @param value {@link org.objectweb.asm.tree.MethodNode#annotationDefault}
	 * @return textual representation of the value
	 */
	public String getAnnotationValue(Object value) {
		buf.setLength(0);
		addAnnotationValue(null, value);
		return buf.toString();
	}

	private void addAnnotationNode(String desc, List values) {
		if (desc != null) {
			buf.append("@").append(Type.getType(desc).getClassName());
			if (values != null) {
				buf.append("(");
				for (int i = 0; i < values.size(); i += 2) {
					addComma(i);
					addAnnotationValue((String) values.get(i), values.get(i + 1));
				}
				buf.append(")");
			}
		}
	}

	private void addAnnotationValue(String name, Object value) {
		if (name != null) {
			buf.append(name).append("=");
		}
		if (value instanceof List<?>) {
			buf.append('{');
			for (int i = 0; i < ((List) value).size(); i++) {
				addComma(i);
				addAnnotationValue(null, ((List) value).get(i));
			}
			buf.append('}');
		} else if (value instanceof String[]) {
			buf.append(Type.getType(((String[]) value)[0]).getClassName()).append(".").append(((String[]) value)[1]);
		} else if (value instanceof String) {
			buf.append("\"").append(value).append("\"");
		} else if (value instanceof Character) {
			buf.append("\'").append(value).append("\'");
		} else if (value instanceof Type) {
			buf.append(((Type) value).getClassName()).append(".class");
		} else if (value instanceof AnnotationNode) {
			addAnnotationNode(((AnnotationNode) value).desc, ((AnnotationNode) value).values);
		} else {
			buf.append(value);
		}
	}

	private void addComma(int currentPosition) {
		if (currentPosition > 0)
			buf.append(", ");
	}
}