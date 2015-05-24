package com.thesis.file;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;

public class ReaderTest {

	private static String TEST_FOLDER = "testData" + File.separator;
    private Reader mReader;

    @Before
    public void setUp() throws Exception {
        mReader = new Reader(TEST_FOLDER);
    }

    @Test
    public void testPrecondition() {
        assertNotNull("Reader cannot be null", mReader);
    }

    @Test
    public void testOpenFile() throws FileNotFoundException {
        assertNotNull(mReader.openClassFile("file"));
    }

    @Test(expected = FileNotFoundException.class)
    public void testOpenFile_wrongFile() throws FileNotFoundException {
        assertNotNull(mReader.openClassFile("nonExistentFile"));
    }

    @Test
    public void testOpenFolder() {

        Path folder = mReader.openDirectory();
        assertNotNull(folder);
        assertTrue("Folder is not a directory",Files.isDirectory(folder));
    }

    @Test
    public void testListAllFiles_realFolder() {
        List<Path> files = mReader.listAllFiles(null);
        assertNotEquals(0, files.size());
    }

    @Test
    public void testListAllFiles_filesOnly() throws Exception {
        List<Path> files = mReader.listAllFiles(null);

        for (Path file : files) {
            assertTrue(Files.isRegularFile(file));
        }
    }
}
