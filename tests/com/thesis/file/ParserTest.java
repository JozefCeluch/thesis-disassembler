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

import static org.junit.Assert.assertEquals;

@RunWith(value = JUnitParamsRunner.class)
public class ParserTest {

	private static String TEST_FOLDER = "testData/";
	private static String RESULTS_FOLDER = "testData/expectedResults/";
	private final static Parser PARSER = new Parser(TEST_FOLDER);

	@Test
	@Parameters(method = "getAllClasses")
	public void testAll(String name){
		// javaClassText(expected), compileAndParseClass(expected, mParser)
		assertEquals("Classes do not equal", javaClassText(name), compileAndParseClass(name, PARSER));
	}

	public List<Object[]> getAllClasses() {
		return getFilteredClasses(pathname -> pathname.isFile() && pathname.getPath().endsWith(".java"));
	}

	@Test
	@Parameters(method = "getInsnNodeClasses")
	public void testInsnNode(String name){
		// javaClassText(expected), compileAndParseClass(expected, mParser)
		assertEquals("Classes do not equal", javaClassText(name), compileAndParseClass(name, PARSER));
	}

	public List<Object[]> getInsnNodeClasses() {
		return getFilteredClasses(file -> file.isFile()
				&& file.getPath().endsWith(".java") && file.getPath().contains("InsnNode_"));
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

	private static String javaClassText(String fileName) {
		byte[] fileContents = new byte[0];
		try {
			fileContents = Files.readAllBytes(Paths.get(RESULTS_FOLDER + fileName + ".java"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(fileContents);
	}

	private static String compileAndParseClass(String name, Parser parser) {
		Process process;
		try {
			System.out.println("COMPILING: " + name);
			process = Runtime.getRuntime().exec("javac -g " + TEST_FOLDER + name + ".java");
			printLines(name + " stderr:", process.getErrorStream());
			process.waitFor();
		} catch (IOException | InterruptedException  e) {
			System.out.println("Compilation unsuccessful");
			return null;
		}
		if (process.exitValue() != 0) {
			System.out.println("COMPILATION ERROR: " + name + ", " + process.exitValue());
		} else {
			System.out.println("COMPILATION SUCCESS: " + name);
		}
		System.out.println("PARSING: " + name);
		return parser.parseClassFile(name + ".class");
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
