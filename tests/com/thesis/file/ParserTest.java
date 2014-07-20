package com.thesis.file;

import org.apache.bcel.classfile.JavaClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParserTest {

    Parser mParser;

    @Before
    public void setUp() throws Exception {
        mParser = new Parser("tests/example");
    }

    @Test
    @Ignore
    public void testParseClassFile_correctInput() throws IOException {
        mParser.parseClassFile("Atom.class");
//        assertNotNull(javaClass);
//        assertEquals("com.thesis.Main", javaClass.getClassName());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseClassFile_incorrectInput() throws Exception {
        mParser.parseClassFile("Atom");
    }
}
