package com.thesis.file;

import org.apache.bcel.classfile.JavaClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParserTest {

    Parser mParser;

    @Before
    public void setUp() throws Exception {
        mParser = new Parser("tests");
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseClassFile_incorrectInput() throws Exception {
        mParser.parseClassFile("Atom");
    }

    @Test
    public void testClassParsed() throws Exception {
        String classText = mParser.parseClassFile("EmptyClass.class");
        assertNotNull(classText);
    }

    @Test
    public void testParseEmptyClass() throws Exception {
        String result = mParser.parseClassFile("EmptyClass.class");
        String expected = javaClassText("EmptyClass.java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyInterface() throws Exception {
        String result = mParser.parseClassFile("EmptyInterface.class");
        String expected = javaClassText("EmptyInterface.java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyInterfaceAnnotation() throws Exception {
        String result = mParser.parseClassFile("EmptyInterfaceAnnotation.class");
        String expected = javaClassText("EmptyInterfaceAnnotation.java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyDeprecatedClass() throws Exception {
        String result = mParser.parseClassFile("EmptyDeprecatedClass.class");
        String expected = javaClassText("EmptyDeprecatedClass.java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyDeprecatedInterface() throws Exception {
        String result = mParser.parseClassFile("EmptyDeprecatedInterface.class");
        String expected = javaClassText("EmptyDeprecatedInterface.java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyEnum() throws Exception {
        String result = mParser.parseClassFile("EmptyEnum.class");
        String expected = javaClassText("EmptyEnum.java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyClassWithInterfaces() throws Exception {
        String result = mParser.parseClassFile("EmptyClassWithInterfaces.class");
        String expected = javaClassText("EmptyClassWithInterfaces.java");
        assertEquals(expected, result);
    }

    private static String javaClassText(String fileName) throws IOException {
        byte[] fileContents = Files.readAllBytes(Paths.get("tests/" + fileName));
        return new String(fileContents).trim();
    }

}
