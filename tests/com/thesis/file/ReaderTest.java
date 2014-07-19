package com.thesis.file;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ReaderTest {

    private Reader mReader;

    @Before
    public void setUp() throws Exception {
        mReader = new Reader("tests");
        mReader.setDirectory("tests");
    }

    @Test
    public void testPrecondition() {
        assertNotNull("Reader cannot be null", mReader);
    }

    @Test
    public void testOpenFile() throws FileNotFoundException {
        assertNotNull(mReader.openFile("file"));
    }

    @Test(expected = FileNotFoundException.class)
    public void testOpenFile_wrongFile() throws FileNotFoundException {
        assertNotNull(mReader.openFile("nonExistentFile"));
    }

    @Test
    public void testOpenFolder() {

        Path folder = mReader.openDirectory();
        assertNotNull(folder);
        assertTrue("Folder is not a directory",Files.isDirectory(folder));
    }



}
