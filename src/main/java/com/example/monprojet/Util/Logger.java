package com.example.monprojet.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    public static void logInfo(String message) {
        System.out.println("[INFO] " + timestamp() + " - " + message);
    }

    public static void logError(String message, Exception e) {
        System.err.println("[ERROR] " + timestamp() + " - " + message);
        e.printStackTrace();
    }

    private static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
