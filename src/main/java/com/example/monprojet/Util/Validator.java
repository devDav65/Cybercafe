package com.example.monprojet.Util;

import java.util.regex.Pattern;

public class Validator {

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (email == null)
            return false;
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(regex, email);
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isPositive(int number) {
        return number > 0;
    }
}
