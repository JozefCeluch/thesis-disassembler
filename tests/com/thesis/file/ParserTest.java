package com.thesis.file;

import org.apache.bcel.classfile.JavaClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParserTest {

    Parser mParser;

    @Before
    public void setUp() throws Exception {
        mParser = new Parser("tests");
    }

    @Test
    @Ignore
    public void testParseClassFile_correctInput() throws IOException {
        mParser.parseClassFile("Main.class");
//        assertNotNull(javaClass);
//        assertEquals("com.thesis.Main", javaClass.getClassName());
    }

    @Test(expected = IOException.class)
    public void testParseClassFile_incorrectInput() throws IOException {
        mParser.parseClassFile("Main");
    }
}
