package com.thesis.common;

import java.io.IOException;
import java.io.Writer;

/**
 * A general interface for classes that can be written out into Java code
 */
public interface Writable {

	void write(Writer writer) throws IOException;
}
