package com.thesis.file;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Reader {

    private String mDirectory;

    public Reader(String directory) {
        mDirectory = directory;
    }

    InputStream openFile(String fileName) throws FileNotFoundException{
        InputStream stream = null;

        stream = new FileInputStream(mDirectory + File.separator + fileName);
        return stream;
    }


    Path openDirectory() {
        Path directory = Paths.get(mDirectory);
        if (Files.isDirectory(directory)) {
            return directory;
        }
        return null;
    }

    /*
    todo create folder, create file, unpack zip (jar), list .class files
     */





    public void setDirectory(String directory) {
        mDirectory = directory;
    }
}
