package com.example.monprojet.Util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    public static void writeToFile(String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes());
    }

    public static String readFromFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}
