package com.thesis.file;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(value = JUnitParamsRunner.class)
public class ParserTest {

	private static String TEST_FOLDER = "testData" + File.separator;
	private static String RESULTS_FOLDER = "testData" + File.separator + "expectedResults" + File.separator;

	@Test
	@Parameters({"ClassWithBoolExpressions", "AnotherEmptyInterface", "ClassWithNumericExpressions", "EmptyDeprecatedClass",
			"EmptyDeprecatedInterface", "EmptyEnum", "EmptyInterface", "ClassWithInnerClasses", "ComplexVariableNames"})
	public void testUngroupedClasses(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, new Parser(TEST_FOLDER)));
	}

	@Test
	@Parameters(method = "getInsnNodeClasses")
	public void testInsnNode(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, new Parser(TEST_FOLDER)));
	}

	public List<Object[]> getInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains(File.separator + "InsnNode_"));
	}

	@Test
	@Parameters(method = "getFieldInsnNodeClasses")
	public void testFieldInsnNodeClasses(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, new Parser(TEST_FOLDER)));
	}

	public List<Object[]> getFieldInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains(File.separator + "FieldInsnNode_"));
	}

	@Test
	@Parameters(method = "getTypeInsnNodeClasses")
	public void testTypeInsnNodeClasses(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, new Parser(TEST_FOLDER)));
	}

	public List<Object[]> getTypeInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains(File.separator + "TypeInsnNode_"));
	}

	@Test
	@Parameters(method = "getMethodInsnNodeClasses")
	public void testMethodInsnNodeClasses(String name){
		assertEquals("Classes do not equal", getJavaClassContent(name), compileAndParseClass(name, new Parser(TEST_FOLDER)));
	}

	public List<Object[]> getMethodInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains(File.separator + "MethodInsnNode_"));
	}

	@Test
	@Parameters(method = "param1, param2, param3, param4, param5, param6, param7, param8, param9")
	public void testClassesWithDependencies(String name, String dependencies) {
		String compileString = TEST_FOLDER + name + ".java " + dependencies;
		if (compileClass(compileString) != 0 ) {
			fail("COMPILATION FAILED");
		}
		assertEquals("Classes do not equal", getJavaClassContent(name), new Parser(TEST_FOLDER).parseClassFile(name + ".class"));
	}
	public Object param1(){return $($("EmptyClassWithInterfaces", makeDependencyString("EmptyInterface", "AnotherEmptyInterface")));}
	public Object param2(){return $($("EmptyInterfaceAnnotation", makeDependencyString("EmptyEnum")));}
	public Object param3(){return $($("SimpleAnnotation", makeDependencyString("RepeatableAnnotation")));}
	public Object param4(){return $($("RepeatableAnnotation", makeDependencyString("SimpleAnnotation")));}
	public Object param5(){return $($("EmptyClassWithComplexAnnotation", makeDependencyString("EmptyInterface", "ComplexAnnotation", "EmptyInterfaceAnnotation", "EmptyEnum")));}
	public Object param6(){return $($("EmptyClass", makeDependencyString("EmptyInterface", "EmptyInterfaceAnnotation", "EmptyEnum")));}
	public Object param7(){return $($("ClassWithMethods", makeDependencyString("SimpleAnnotation", "RepeatableAnnotation")));}
	public Object param8(){return $($("ClassWithFields", makeDependencyString("SimpleAnnotation", "RepeatableAnnotation")));}
	public Object param9(){return $($("ComplexAnnotation", makeDependencyString("EmptyInterfaceAnnotation", "EmptyEnum")));}

	private String makeDependencyString(String... deps) {
		String depString = "";
		for (int i=0; i < deps.length; i++) {
			deps[i] = TEST_FOLDER + deps[i] + ".java ";
			depString += deps[i];
		}
		return depString;
	}

	private List<Object[]> getFilteredClasses(final FileFilter filter) {
		File srcFolder = new File(TEST_FOLDER);
		if (!srcFolder.isDirectory()) throw new RuntimeException("Not a folder");

		File[] files = srcFolder.listFiles(filter);

		return Arrays.asList(files).stream()
				.map(f -> f.getName().replace(".java", ""))
				.map((String name) -> new Object[] {name})
				.collect(Collectors.toList());
	}

	private static String getJavaClassContent(String fileName) {
		byte[] fileContents = new byte[0];
		try {
			fileContents = Files.readAllBytes(Paths.get(RESULTS_FOLDER + fileName + ".java"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(fileContents);
	}

	private static String compileAndParseClass(String name, Parser parser) {
		int compilationResult = compileClass(TEST_FOLDER + name + ".java");
		if (compilationResult != 0) {
			System.out.println("COMPILATION ERROR: " + name + ", " + compilationResult);
			return null;
		}
		System.out.println("COMPILATION SUCCESS: " + name);
		System.out.println("PARSING: " + name);
		return parser.parseClassFile(name + ".class");
	}

	private static int compileClass(String name) {
		Process process;
		try {
			System.out.println("COMPILING: " + name);
			process = Runtime.getRuntime().exec("javac -g " + name);
			printLines(name + " stderr:", process.getErrorStream());
			process.waitFor();
		} catch (IOException | InterruptedException  e) {
			System.out.println("Compilation unsuccessful");
			return -1;
		}
		return process.exitValue();
	}

	private static void printLines(String name, InputStream ins) throws IOException{
		String line;
		BufferedReader in = new BufferedReader(
				new InputStreamReader(ins));
		while ((line = in.readLine()) != null) {
			System.out.println(name + " " + line);
		}
	}
}
