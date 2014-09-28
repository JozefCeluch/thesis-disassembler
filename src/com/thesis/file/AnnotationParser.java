package com.thesis.file;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.List;

public class AnnotationParser {

	private StringBuffer buf;

	public AnnotationParser() {
		buf = new StringBuffer();
	}

	public String getAnnotations(List<AnnotationNode> annotations, String separator) {
		if (annotations == null) return "";
		buf.setLength(0);
		for (AnnotationNode annotation : annotations) {
			addAnnotationNode(annotation.desc, annotation.values);
			buf.append(separator);
		}
		return buf.toString();
	}

	public String getAnnotationValue(Object value) {
		buf.setLength(0);
		addAnnotationValue(null, value);
		return buf.toString();
	}

	private void addAnnotationNode(String desc, List values) {
		if (desc != null) {
			buf.append("@").append(Util.javaObjectName(Util.getType(desc))); //todo more complicated annotations?
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
			buf.append(Util.getType(((String[]) value)[0])).append(".").append(((String[]) value)[1]);
		} else if (value instanceof String) {
			buf.append("\"").append(value).append("\"");
		} else if (value instanceof Character) {
			buf.append("\'").append(value).append("\'");
		} else if (value instanceof Type) {
			buf.append(Util.javaObjectName(((Type) value).getClassName())).append(".class");
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