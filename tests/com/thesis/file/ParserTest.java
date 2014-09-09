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
        mParser = new Parser("tests");
    }

    @Test
    @Ignore
    public void testParseClassFile_correctInput() throws IOException {
//        JavaClass javaClass = mParser.parseClassFile("Atom.class");
//        assertNotNull(javaClass);
//        assertEquals("Atom", javaClass.getClassName());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseClassFile_incorrectInput() throws Exception {
        mParser.parseClassFile("Atom");
    }

    @Test
    public void testClassParsed() throws Exception {
        mParser.parseClassFile("EmptyClass.class");
    }


}
