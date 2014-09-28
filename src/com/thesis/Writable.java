package com.thesis;

import java.io.IOException;
import java.io.Writer;

public interface Writable {

	public void write(Writer writer) throws IOException;
}
