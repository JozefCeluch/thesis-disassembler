package com.thesis.file;


import com.thesis.exception.DecompilerException;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Reader {

    private String mDirectoryName;
    private Path mPath;

    public Reader(String directory) {
        mDirectoryName = directory;
        mPath = openDirectory();
    }

    InputStream openClassFile(String fileName) throws FileNotFoundException {
        return new FileInputStream(mDirectoryName + File.separator + fileName);
    }

    Path openDirectory() {
        Path directory = Paths.get(mDirectoryName);
        if (Files.isDirectory(directory)) {
            return directory;
        }
        return null;
    }

    List<Path> listAllFiles(Path path) {
        if (path == null) {
            path = mPath;
        }
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    files.add(file);
                }
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(e);
        }
        return files;
    }

    Path extractClassFilesFromJar(String jarName) throws DecompilerException{
        JarFile jarFile;
        try {
            jarFile = new JarFile(mPath.toString() + File.separator + jarName);
        } catch (IOException e) {
            throw new DecompilerException("Problem while creating a jar file object",e);
        }

        Path folder = createEmptyFolder(jarName);

        for (Enumeration<JarEntry> entry = jarFile.entries(); entry.hasMoreElements(); ) {
            JarEntry file = entry.nextElement();
            if (!file.getName().endsWith(".class")) {
                continue;
            }
            Path path = Paths.get(folder + File.separator + file.getName());

            try (InputStream inputStream = jarFile.getInputStream(file);
                 OutputStream outputStream = Files.newOutputStream(path)) {

                while (inputStream.available() > 0) {
                    outputStream.write(inputStream.read());
                }

            } catch (IOException e) {
                throw new DecompilerException("Problem while reading a jar",e);
            }
        }
        return folder;
    }

    private Path createEmptyFolder(String name) throws DecompilerException {
        if (name.endsWith(".jar")) {
            name = name.substring(0, name.indexOf(".jar"));
        }
        Path folder = Paths.get(mPath.toString() + File.separator + name);
        try {
            if (!Files.exists(folder)) {
                folder = Files.createDirectory(folder);
            } else {
                for (Path file: listAllFiles(folder)) {
                    Files.deleteIfExists(file);
                }
            }
        } catch (IOException e) {
            throw new DecompilerException("Error while creating a folder", e);
        }

        return folder;
    }
}
