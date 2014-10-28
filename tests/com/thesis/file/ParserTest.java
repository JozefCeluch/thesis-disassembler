package com.thesis.file;

import org.apache.bcel.classfile.JavaClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParserTest {

	private static String TEST_FOLDER = "testData/";
    Parser mParser;

    @Before
    public void setUp() throws Exception {
        mParser = new Parser(TEST_FOLDER);
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
		String className = "EmptyClass";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyInterface() throws Exception {
		String className = "EmptyInterface";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyInterfaceAnnotation() throws Exception {
		String className = "EmptyInterfaceAnnotation";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyDeprecatedClass() throws Exception {
		String className = "EmptyDeprecatedClass";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyDeprecatedInterface() throws Exception {
		String className = "EmptyDeprecatedInterface";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyEnum() throws Exception {
		String className = "EmptyEnum";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseEmptyClassWithInterfaces() throws Exception {
		String className = "EmptyClassWithInterfaces";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
        assertEquals(expected, result);
    }

    @Test
    public void testParseClassWithFields() throws Exception {
		String className = "ClassWithFields";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
        assertEquals(expected, result);
    }

	@Test
	public void testParseClassWithMethods() throws Exception {
		String className = "ClassWithMethods";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
		assertEquals(expected, result);
	}

	@Test
	public void testParseEmptyClassWithInnerClass() throws Exception {
		String className = "EmptyClassWithInnerClass";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
		assertEquals(expected, result);
	}

	@Test
	public void testParseEmptyClassWithComplexAnnotation() throws Exception {
		String className = "EmptyClassWithComplexAnnotation";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
		assertEquals(expected, result);
	}

	@Test
	public void testParseClassWithNumericExpressions() throws Exception {
		String className = "ClassWithNumericExpressions";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
		assertEquals(expected, result);
	}

	@Test
	public void testParseClassWithBoolExpressions() throws Exception {
		String className = "ClassWithBoolExpressions";
		compileClass(className);
		String result = mParser.parseClassFile(className + ".class");
		String expected = javaClassText(className + ".java");
		assertEquals(expected, result);
	}

    private static String javaClassText(String fileName) throws IOException {
        byte[] fileContents = Files.readAllBytes(Paths.get(TEST_FOLDER + fileName));
        return new String(fileContents);
    }

	private static void printLines(String name, InputStream ins) throws Exception {
		String line;
		BufferedReader in = new BufferedReader(
				new InputStreamReader(ins));
		while ((line = in.readLine()) != null) {
			System.out.println(name + " " + line);
		}
	}

	private static void compileClass(String name) throws Exception {
		Process process = Runtime.getRuntime().exec("javac -g " + TEST_FOLDER + name + ".java");
		printLines(name + " stderr:", process.getErrorStream());
		process.waitFor();
		if (process.exitValue() != 0) {
			System.out.println("COMPILATION ERROR: " + name + ", " + process.exitValue());
		} else {
			System.out.println("COMPILATION SUCCESS: " + name);
		}
	}

}
