package com.example.monprojet.Util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Crypter un mot de passe
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Vérifier un mot de passe
    public static boolean checkPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
