package com.thesis.file;


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
        InputStream stream = null;

        stream = new FileInputStream(mDirectoryName + File.separator + fileName);
        return stream;
    }


    Path openDirectory() {
        Path directory = Paths.get(mDirectoryName);
        if (Files.isDirectory(directory)) {
            return directory;
        }
        return null;
    }

    public void setDirectoryName(String directoryName) {
        mDirectoryName = directoryName;
        mPath = openDirectory();
    }

    public List<Path> listAllFiles(Path path) {
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

    private Path createEmptyFolder(String name) {
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
            //todo throw exception
            e.printStackTrace();
        }

        return folder;
    }

    public Path extractClassFilesFromJar(String jarName) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(mPath.toString() + File.separator + jarName);
        } catch (IOException e) {
            e.printStackTrace();
            //todo throw own exception
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
                e.printStackTrace();
            }
        }
        return folder;
    }
}
