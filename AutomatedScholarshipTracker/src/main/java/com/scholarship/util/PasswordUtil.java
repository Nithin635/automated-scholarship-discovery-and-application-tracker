package com.scholarship.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password hashing helper backed by jBCrypt. Passwords are NEVER stored
 * or compared as plain text anywhere in this application.
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /** Hashes a plain-text password for storage in users.password_hash. */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    /** Verifies a login attempt's plain-text password against the stored hash. */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) return false;
        try {
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (IllegalArgumentException e) {
            // Malformed hash in the database — fail closed.
            return false;
        }
    }
}
