package com.thesis.common;

import java.io.IOException;
import java.io.Writer;

/**
 * A general interface for classes that can be written out into Java code
 */
public interface Writable {

	/**
	 * Writes the relevant parts of the object to the provided writer
	 * @param writer a writer instance
	 * @throws IOException in case of an error
	 */
	void write(Writer writer) throws IOException;
}
